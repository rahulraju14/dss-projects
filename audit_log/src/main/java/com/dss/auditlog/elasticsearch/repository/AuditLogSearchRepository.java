package com.dss.auditlog.elasticsearch.repository;

import com.dss.auditlog.elasticsearch.documents.AuditLogDoc;
import com.dss.auditlog.utils.DashboardDataHolder;
import io.jmix.core.LoadContext;
import io.jmix.core.querycondition.Condition;
import io.jmix.core.querycondition.LogicalCondition;
import io.jmix.core.querycondition.PropertyCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.data.client.orhlc.OpenSearchAggregations;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.RangeQueryBuilder;
import org.opensearch.search.aggregations.Aggregation;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.opensearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.opensearch.search.aggregations.bucket.filter.ParsedFilter;
import org.opensearch.search.aggregations.metrics.Max;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsIterator;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.dss.auditlog.elasticsearch.documents.AuditLogDoc.Fields.*;
import static com.dss.auditlog.queries.DashboardQuery.*;
import static io.jmix.core.querycondition.PropertyCondition.Operation.*;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.opensearch.index.query.QueryBuilders.*;

@Slf4j
@Repository
public class AuditLogSearchRepository {
    private static final List<String> NOT_SUPPORTED_OPERATIONS = Arrays.asList(IN_INTERVAL, IS_COLLECTION_EMPTY, MEMBER_OF_COLLECTION, NOT_MEMBER_OF_COLLECTION);
    private static final List<String> KEYWORD_FIELDS = Arrays.asList(ENTRY_NO, TABLE_NO, CREATED_DATE, TYPE_OF_CHANGE, SYSTEM_CREATED_DATE, SYSTEM_LAST_MODIFIED_DATE);
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String MAX_ENTRY_NO = "max_entryNo";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private final ElasticsearchOperations operations;

    @Autowired
    public AuditLogSearchRepository(ElasticsearchOperations operations) {
        this.operations = operations;
    }


    public List<AuditLogDoc> searchAuditLogDocs(LoadContext<AuditLogDoc> loadContext, LocalDate from, LocalDate to) {
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            log.info("searchAuditLogDocs... START");
            LoadContext.Query jmixQuery = loadContext.getQuery();

            int pageSize = Objects.requireNonNull(jmixQuery).getMaxResults();
            int firstResult = jmixQuery.getFirstResult();
            log.info("-- Records skipped: {}", firstResult);

            try (SearchHitsIterator<AuditLogDoc> auditLogScrollingData = getAuditLogScrollingData(loadContext, from, to, pageSize)) {
                List<AuditLogDoc> auditLogDocList = auditLogScrollingData.stream()
                        .skip(firstResult)
                        .limit(pageSize)
                        .map(SearchHit::getContent)
                        .collect(Collectors.toList());

                log.info("=== Total records fetched from DB: {}", auditLogScrollingData.getTotalHits());
                stopWatch.stop();
                log.info("searchAuditLogDocs... END, {} records in {}ms", auditLogDocList.size(), stopWatch.getTotalTimeMillis());
                return auditLogDocList;
            }
        } catch (Exception ex) {
            stopWatch.stop();
            log.error("Error while fetching data..", ex);
            throw new RuntimeException(ex);
        }
    }

    private void addCondition(BoolQueryBuilder builder, PropertyCondition propertyCondition) {
        Object propValue = Objects.requireNonNull(propertyCondition.getParameterValue());
        String property = propertyCondition.getProperty();
        String operation = propertyCondition.getOperation();
        log.debug("Condition:{}, {}, {}", property, operation, propertyCondition.getParameterValue());
        boolean isKeyword = KEYWORD_FIELDS.contains(property);
        //else
        switch (operation) {
            case EQUAL: {
                builder.must(termQuery(isKeyword ? property : property + ".keyword", propValue.toString()));
                break;
            }
            case NOT_EQUAL: {
                builder.mustNot(termQuery(isKeyword ? property : property + ".keyword", propValue.toString()));
                break;
            }
            case GREATER: {
                builder.must(rangeQuery(property).gt(propValue));
                break;
            }
            case GREATER_OR_EQUAL: {
                builder.must(rangeQuery(property).gte(propValue));
                break;
            }
            case LESS: {
                builder.must(rangeQuery(property).lt(propValue));
                break;
            }
            case LESS_OR_EQUAL: {
                builder.must(rangeQuery(property).lte(propValue));
                break;
            }
            case CONTAINS: {
                String value = (String) propValue;
                String regex = String.format(".*%s.*", value);
                builder.must(regexpQuery(property + ".keyword", regex).caseInsensitive(true));
                break;
            }
            case NOT_CONTAINS: {
                String value = (String) propValue;
                String regex = String.format(".*%s.*", value);
                builder.mustNot(regexpQuery(property + ".keyword", regex).caseInsensitive(true));
                break;
            }
            case IS_SET: {
                boolean value = (boolean) propValue;
                if (value) {
                    builder.must(wildcardQuery(property, "*"));
                } else {
                    builder.must(boolQuery().must(existsQuery(property)).mustNot(wildcardQuery(property, "*")));
                }
                break;
            }
            case STARTS_WITH: {
                String value = (String) propValue;
                String regex = String.format("%s.*", value);
                builder.must(regexpQuery(property + ".keyword", regex).caseInsensitive(true));
                break;
            }
            case ENDS_WITH: {
                String value = (String) propValue;
                String regex = String.format(".*%s", value);
                builder.must(regexpQuery(property + ".keyword", regex).caseInsensitive(true));
                break;
            }
            case IN_LIST: {
                if (propValue instanceof Collection<?>) {
                    List<String> values = ((Collection<?>) propValue).stream().filter(Objects::nonNull).map(Object::toString).toList();
                    if (CollectionUtils.isNotEmpty(values)) {
                        builder.must(termsQuery(isKeyword ? property : property + ".keyword", values));
                    }
                    break;
                } else {
                    throw new IllegalArgumentException(String.format("Expected a Collection, but got: %s", propValue.getClass().getName()));
                }
            }
            case NOT_IN_LIST: {
                List<String> values = ((List<?>) propValue).stream().map(Object::toString).collect(Collectors.toList());
                builder.mustNot(termsQuery(property, values));
                break;
            }
            default:
                throw new UnsupportedOperationException(String.format("Operation '%s' is not supported!!", operation));
        }

    }

    private Query createNativeQuery(LoadContext<AuditLogDoc> loadContext, LocalDate from, LocalDate to, Pageable pageable) {
        LoadContext.Query jmixQuery = loadContext.getQuery();
        BoolQueryBuilder builder = boolQuery();
        if (from != null || to != null) {
            RangeQueryBuilder rqb = rangeQuery(CREATED_DATE).format(DATE_FORMAT);
            if (from != null) {
                rqb.from(DATE_TIME_FORMATTER.format(from));
            }
            if (to != null) {
                rqb = rqb.to(DATE_TIME_FORMATTER.format(to));
            }
            builder.must(rqb);
        }
        LogicalCondition logicalCondition = (LogicalCondition) Objects.requireNonNull(jmixQuery).getCondition();
        buildPropertyConditionQuery(builder, logicalCondition);

        return new NativeSearchQueryBuilder().withQuery(builder)
                .withPageable(pageable)
                .build();
    }

    public Long getCount(LoadContext<AuditLogDoc> loadContext, LocalDate from, LocalDate to) {
        return operations.count(createNativeQuery(loadContext, from, to, Pageable.unpaged()), AuditLogDoc.class);
    }

    public Map<String, List<String>> fetchUniqueValues(List<String> fields) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String strFields = fields.toString();
        log.info("fetchUniqueValues({})... START", strFields);

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        for (String field : fields) {
            boolean isText = !KEYWORD_FIELDS.contains(field);
            queryBuilder.withAggregations(AggregationBuilders.terms(field).field(isText ? field + ".keyword" : field));
        }
        NativeSearchQuery query = queryBuilder.withMaxResults(0).build();

        SearchHits<AuditLogDoc> searchHits = operations.search(query, AuditLogDoc.class);
        OpenSearchAggregations searchAggregations = (OpenSearchAggregations) searchHits
                .getAggregations();
        Map<String, Aggregation> aggregationMap = Objects.requireNonNull(searchAggregations).aggregations().asMap();
        Map<String, List<String>> results = new HashMap<>();
        fields.forEach(docField -> {
            MultiBucketsAggregation aggregation = (MultiBucketsAggregation) aggregationMap.get(docField);
            List<String> list = aggregation.getBuckets().stream()
                    .map(MultiBucketsAggregation.Bucket::getKeyAsString).filter(r -> StringUtils.equalsIgnoreCase(docField, "documentType") ||
                            StringUtils.isNotBlank(r)).toList();
            results.put(docField, list);
        });
        stopWatch.stop();
        log.info("fetchUniqueValues({})... END, {} records in {}ms", strFields, results.size(), stopWatch.getTotalTimeMillis());
        return results;
    }

    public Long maxEntryNo() {
        long result = 0;
        try {
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder().withMaxResults(0);
            NativeSearchQuery query = queryBuilder.withAggregations(
                    AggregationBuilders.max(MAX_ENTRY_NO).field(ENTRY_NO)).build();
            SearchHits<AuditLogDoc> searchHits = operations.search(query, AuditLogDoc.class);
            OpenSearchAggregations searchAggregations = (OpenSearchAggregations) searchHits.getAggregations();
            Max aggregation = (Max) Objects.requireNonNull(searchAggregations).aggregations().asMap().get(MAX_ENTRY_NO);
            Double value = aggregation.getValue();
            result = value.longValue();
        } catch (Exception ex) {
            log.error("Error while getting max entry no", ex);
        }
        return result;
    }

    private void buildPropertyConditionQuery(BoolQueryBuilder builder, LogicalCondition logicalCondition) {
        logicalCondition.getConditions().stream()
                .flatMap(this::flattenNestedLogicalConditions)
                .filter(PropertyCondition.class::isInstance)
                .map(PropertyCondition.class::cast)
                .filter(this::isValidCondition)
                .forEach(propertyCondition -> addCondition(builder, propertyCondition));
    }

    private Stream<Condition> flattenNestedLogicalConditions(Condition condition) {
        if (condition instanceof LogicalCondition) {
            return ((LogicalCondition) condition).getConditions().stream().flatMap(this::flattenNestedLogicalConditions);
        } else {
            return Stream.of(condition);
        }
    }

    private boolean isValidCondition(PropertyCondition condition) {
        return Objects.nonNull(condition.getParameterValue()) && !NOT_SUPPORTED_OPERATIONS.contains(condition.getOperation());
    }

    public SearchHitsIterator<AuditLogDoc> getAuditLogScrollingData(LoadContext<AuditLogDoc> loadContext, LocalDate from, LocalDate to, int batchSize) {
        List<Sort.Order> sortOrders = getSortOrder(loadContext.getQuery());
        PageRequest pageable = PageRequest.of(0, batchSize, Sort.by(sortOrders));
        Query query = createNativeQuery(loadContext, from, to, pageable);
        return operations.searchForStream(query, AuditLogDoc.class);
    }

    private List<Sort.Order> getSortOrder(LoadContext.Query jmixQuery) {
        List<Sort.Order> sortOrders = new ArrayList<>();
        Sort.Direction direction = null;
        String property = null;
        if (jmixQuery.getSort() != null && isNotEmpty(jmixQuery.getSort().getOrders())) {
            io.jmix.core.Sort.Order first = jmixQuery.getSort().getOrders().stream().findFirst().get();
            direction = first.getDirection() == io.jmix.core.Sort.Direction.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
            property = first.getProperty();
        } else {
            direction = Sort.Direction.DESC;
            property = CREATED_DATE;
        }
        if (!KEYWORD_FIELDS.contains(property)) {
            property = property + ".keyword";
        }
        sortOrders.add(new Sort.Order(direction, property).ignoreCase().nullsLast());
        sortOrders.add(new Sort.Order(direction, ENTRY_NO));
        return sortOrders;
    }

    public Map<String, Long> getTypeOfChangeRecords(LocalDate from, LocalDate to) {
        Map<String, Long> typeOfChangeMap = new HashMap<>();
        SearchHits<AuditLogDoc> searchHits = operations.search(fetchAndGroupByTypeOfChange(from, to), AuditLogDoc.class);
        processAggregations((OpenSearchAggregations) searchHits.getAggregations(), typeOfChangeMap);
        return typeOfChangeMap;
    }

    public Map<String, Long> getDocumentTypeRecords(LocalDate from, LocalDate to) {
        Map<String, Long> documentTypeMap = new HashMap<>();
        SearchHits<AuditLogDoc> searchHits = operations.search(fetchAndGroupByDocumentType(from, to), AuditLogDoc.class);
        processAggregations((OpenSearchAggregations) searchHits.getAggregations(), documentTypeMap);
        return documentTypeMap;
    }

    public Collection<DashboardDataHolder> getUsersByTypeOfChange(LocalDate from, LocalDate to) {
        Map<String, Map<String, Long>> userTypeChangeMap = new HashMap<>();
        SearchHits<AuditLogDoc> searchHits = operations.search(fetchUsersAndGroupByTypeOfChangeAndCreatedBy(from, to), AuditLogDoc.class);
        processAggregations((OpenSearchAggregations) searchHits.getAggregations(), userTypeChangeMap);
        return processAggregationResult(userTypeChangeMap);
    }

    public Collection<DashboardDataHolder> getTableCaptionByTypeOfChange(LocalDate from, LocalDate to) {
        Map<String, Map<String, Long>> tableCaptionResultMap = new HashMap<>();
        SearchHits<AuditLogDoc> searchHits = operations.search(fetchTableCaptionAndGroupByTypeOfChangeAndCaption(from, to), AuditLogDoc.class);
        processAggregations((OpenSearchAggregations) searchHits.getAggregations(), tableCaptionResultMap);
        return processAggregationResult(tableCaptionResultMap);
    }

    private void processAggregations(OpenSearchAggregations openSearchAggregations, Object resultMap) {
        openSearchAggregations.aggregations().asList().forEach(agg -> flattenAggregation(agg, resultMap));
    }

    private void flattenAggregation(Aggregation aggregation, Object resultMap) {
        if (aggregation instanceof ParsedFilter parsedFilter) {
            parsedFilter.getAggregations().asList().forEach(subAgg -> {
                flattenAggregation(subAgg, resultMap);
            });
        } else if (aggregation instanceof MultiBucketsAggregation multiBucketsAggregation) {
            if (isNestedAggregations(multiBucketsAggregation)) {
                processNestedMultiBucketAggregations(multiBucketsAggregation, (Map<String, Map<String, Long>>) resultMap);
            } else {
                processMultiBucketsAggregation(multiBucketsAggregation, (Map<String, Long>) resultMap);
            }
        }
    }

    private boolean isNestedAggregations(MultiBucketsAggregation multiBucketsAggregation) {
        return multiBucketsAggregation.getBuckets().stream().findFirst().stream()
                .anyMatch(bucket -> CollectionUtils.isNotEmpty(bucket.getAggregations().asList()) &&
                        bucket.getAggregations().asList().stream().findFirst().stream().anyMatch(MultiBucketsAggregation.class::isInstance));
    }

    private void processMultiBucketsAggregation(MultiBucketsAggregation multiBucketsAggregation, Map<String, Long> resultMap) {
        multiBucketsAggregation.getBuckets().forEach(bucket -> {
            resultMap.put(bucket.getKeyAsString(), bucket.getDocCount());
        });
    }

    private void processNestedMultiBucketAggregations(MultiBucketsAggregation multiBucketsAggregation, Map<String, Map<String, Long>> resultMap) {
        multiBucketsAggregation.getBuckets().forEach(bucket -> {
            bucket.getAggregations().asList().stream()
                    .filter(MultiBucketsAggregation.class::isInstance)
                    .map(MultiBucketsAggregation.class::cast)
                    .map(MultiBucketsAggregation::getBuckets)
                    .forEach(childBucket -> {
                        Map<String, Long> typeChangeMap = childBucket.stream()
                                .collect(Collectors.toMap(MultiBucketsAggregation.Bucket::getKeyAsString, MultiBucketsAggregation.Bucket::getDocCount));
                        resultMap.put(bucket.getKeyAsString(), typeChangeMap);
                    });
        });
    }

    private Collection<DashboardDataHolder> processAggregationResult(Map<String, Map<String, Long>> userTypeChangeMap) {
        return userTypeChangeMap.entrySet().stream()
                .map(entry -> {
                    DashboardDataHolder dataHolder = new DashboardDataHolder();
                    dataHolder.setTableEntry(entry.getKey());

                    entry.getValue().forEach((typeChange, count) -> {
                        if ("Modification".equalsIgnoreCase(typeChange)) {
                            dataHolder.setModificationCount(count);
                        } else {
                            dataHolder.setDeletionCount(count);
                        }
                    });

                    dataHolder.setTotalCount(Optional.ofNullable(dataHolder.getModificationCount()).orElse(0L)
                                    + Optional.ofNullable(dataHolder.getDeletionCount()).orElse(0L)
                    );

                    return dataHolder;
                })
                .sorted(Comparator.comparingLong(DashboardDataHolder::getTotalCount).reversed()) // Sort in descending order by total count
                .collect(Collectors.toList());
    }
}
