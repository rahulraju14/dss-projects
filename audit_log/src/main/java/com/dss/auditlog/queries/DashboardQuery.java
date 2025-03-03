package com.dss.auditlog.queries;

import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.aggregations.AggregationBuilders;
import org.opensearch.search.aggregations.BucketOrder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DashboardQuery {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static NativeSearchQuery fetchAndGroupByTypeOfChange(LocalDate from, LocalDate to) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        NativeSearchQuery query = queryBuilder.withAggregations(
                        AggregationBuilders.filter("typeOfChange", QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("typeOfChange", ""))
                                        .filter(buildDateRangeQuery(from, to)))
                                .subAggregation(AggregationBuilders.terms("group_by_typeOfChange").field("typeOfChange")
                                        .order(BucketOrder.count(false))
                                        .subAggregation(AggregationBuilders.count("doc_count").field("typeOfChange")))).withMaxResults(0)
                .build();
        return query;
    }

    public static NativeSearchQuery fetchAndGroupByDocumentType(LocalDate from, LocalDate to) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        NativeSearchQuery query = queryBuilder.withAggregations(
                        AggregationBuilders.filter("documentType", QueryBuilders.boolQuery().filter(buildDateRangeQuery(from, to)))
                                .subAggregation(AggregationBuilders.terms("group_by_documentType").field("documentType.keyword")
                                        .order(BucketOrder.count(false))
                                        .subAggregation(AggregationBuilders.count("doc_count").field("documentType.keyword")))).withMaxResults(0)
                .build();
        return query;
    }

    public static NativeSearchQuery fetchUsersAndGroupByTypeOfChangeAndCreatedBy(LocalDate from, LocalDate to) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        NativeSearchQuery query = queryBuilder.withAggregations(
                        AggregationBuilders.filter("typeOfChange", QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("typeOfChange", "Insertion"))).subAggregation(
                                AggregationBuilders.filter("typeOfChange", QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("typeOfChange", ""))
                                                .filter(buildDateRangeQuery(from, to)))
                                        .subAggregation(AggregationBuilders.terms("group_by_createdBy").field("createdBy.keyword").subAggregation(
                                                AggregationBuilders.terms("group_by_typeOfChange").field("typeOfChange")
                                                        .order(BucketOrder.count(false))
                                                        .subAggregation(AggregationBuilders.count("doc_count").field("typeOfChange")))))).withMaxResults(0)
                .build();
        return query;
    }

    public static NativeSearchQuery fetchTableCaptionAndGroupByTypeOfChangeAndCaption(LocalDate from, LocalDate to) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        NativeSearchQuery query = queryBuilder.withAggregations(
                        AggregationBuilders.filter("typeOfChange", QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("typeOfChange", "Insertion"))).subAggregation(
                                AggregationBuilders.filter("typeOfChange", QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("typeOfChange", ""))
                                                .filter(buildDateRangeQuery(from, to)))
                                        .subAggregation(AggregationBuilders.terms("group_by_tableCaption").field("tableCaption.keyword").subAggregation(
                                                AggregationBuilders.terms("group_by_typeOfChange").field("typeOfChange")
                                                        .order(BucketOrder.count(false))
                                                        .subAggregation(AggregationBuilders.count("doc_count").field("typeOfChange")))))).withMaxResults(0)
                .build();
        return query;
    }

    private static QueryBuilder buildDateRangeQuery(LocalDate from, LocalDate to) {
        return Optional.ofNullable(from)
                .map(startDate -> QueryBuilders.rangeQuery("createdDate")
                        .from(DATE_TIME_FORMATTER.format(startDate))
                        .format(DATE_FORMAT)
                        .to(Optional.ofNullable(to)
                                .map(endDate -> DATE_TIME_FORMATTER.format(endDate))
                                .orElse(null)))
                .orElseGet(() -> Optional.ofNullable(to)
                        .map(endDate -> QueryBuilders.rangeQuery("createdDate")
                                .to(DATE_TIME_FORMATTER.format(endDate))
                                .format(DATE_FORMAT))
                        .orElse(null));
    }


}
