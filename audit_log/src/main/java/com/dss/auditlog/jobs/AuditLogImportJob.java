package com.dss.auditlog.jobs;

import com.dss.auditlog.elasticsearch.documents.AuditLogDoc;
import com.dss.auditlog.elasticsearch.repository.AuditLogDocRepository;
import com.dss.auditlog.elasticsearch.repository.AuditLogSearchRepository;
import com.dss.auditlog.rest.dto.AuditLogEntries;
import com.dss.auditlog.rest.dto.AuditLogEntry;
import com.dss.auditlog.rest.service.AuditLogEntriesService;
import io.jmix.core.security.Authenticated;
import io.jmix.core.security.SystemAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class AuditLogImportJob implements Job {
    private static final String FIND_MILLI_SEC_REGEX = "(?<=\\.)\\d+(?=Z)";
    private static final String FIND_WITHOUT_MILLI_SEC_REGEX = "(?<=:)\\d+(?=Z)";
    private static DateTimeFormatter ISO_86_01_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    @Autowired
    private AuditLogEntriesService auditLogEntriesService;
    @Autowired
    private AuditLogDocRepository auditLogDocRepository;
    @Autowired
    private AuditLogSearchRepository searchRepository;
    @Autowired
    private SystemAuthenticator systemAuthenticator;
    @Autowired
    private ElasticsearchOperations operations;

    @Autowired
    private RetryTemplate retryTemplate;

    @Authenticated
    @Override
    public void execute(JobExecutionContext context) {
        try {
            log.info("AuditLogImportJob...START");
            IndexOperations indexOperations = operations.indexOps(AuditLogDoc.class);
//        Settings settings = indexOperations.getSettings(true);
            final int batchSize = 2000;
            boolean hasMore = false;
            long skip = searchRepository.maxEntryNo();
            List<CompletableFuture<Long>> batches = new ArrayList<>();
            do {
                log.info("Fetching AuditLog Entries.. skip:{}", skip);
                AuditLogEntries result = fetchAuditLogEntries(false, batchSize, skip);
                List<AuditLogEntry> entries = result.getEntries();
                hasMore = entries.size() == batchSize;
                if (hasMore) {
                    skip = entries.get(entries.size() - 1).getEntryNo();
                }
                if (!entries.isEmpty()) {
                    log.info("Fetched AuditLog Entries.. from:{}, to:{}", entries.get(0).getEntryNo(), entries.get(entries.size() - 1).getEntryNo());
                    CompletableFuture<Long> batch = saveAll(entries);
                    batches.add(batch);
                }
            } while (hasMore);
            Long count = batches.stream()
                    .map(CompletableFuture::join)
                    .reduce(0L, Long::sum);
            log.info("Total {} Audi logs inserted", count);

            log.info("AuditLogImportJob...END");
        } catch (Exception ex) {
            log.error("Error while importing records: {}", ex);
            throw new RuntimeException(ex);
        }
    }

    private static OffsetDateTime toDate(String str) {
        Pattern pattern = Pattern.compile(FIND_MILLI_SEC_REGEX);
        Matcher matcher = pattern.matcher(str);
        String dateStr = str;
        if (matcher.find()) {
            String milliSeconds = matcher.group();
            milliSeconds = StringUtils.rightPad(milliSeconds, 3, "0");
            dateStr = matcher.replaceFirst(milliSeconds);
        } else {
            //try without milliseconds
            pattern = Pattern.compile(FIND_WITHOUT_MILLI_SEC_REGEX);
            matcher = pattern.matcher(dateStr);
            if (matcher.find()) {
                String seconds = matcher.group();
                String withMilliSeconds = StringUtils.rightPad(seconds + ".", 6, "0");
                dateStr = matcher.replaceFirst(withMilliSeconds);
            } else {
                log.error("Invalid date pattern:{}", dateStr);
                throw new RuntimeException("Unable to parse date:+" + str);
            }
        }
        OffsetDateTime date = OffsetDateTime.parse(dateStr, ISO_86_01_DATE_TIME_FORMATTER);
        return date;
    }

    public static AuditLogDoc fromAuditLogEntry(AuditLogEntry entry) {
        return AuditLogDoc.builder()
                .entryNo(entry.getEntryNo())
                .createdBy(entry.getCreatedBy())
                .createdDate(toDate(entry.getCreatedDate()))
                .tableCaption(entry.getTableCaption())
                .tableNo(entry.getTableNo())
                .documentType(entry.getDocumentType())
                .documentNo(entry.getDocumentNo())
                .fieldCaption(entry.getFieldCaption())
                .typeOfChange(entry.getTypeOfChange())
                .oldValue(entry.getOldValue())
                .newValue(entry.getNewValue())
                .referenceDescription(entry.getReferenceDescription())
                .primaryKeyField1Description(entry.getPrimaryKeyField1Description())
                .primaryKeyField1Value(entry.getPrimaryKeyField1Value())
                .primaryKeyField2Description(entry.getPrimaryKeyField2Description())
                .primaryKeyField2Value(entry.getPrimaryKeyField2Value())
                .primaryKeyField3Description(entry.getPrimaryKeyField3Description())
                .primaryKeyField3Value(entry.getPrimaryKeyField3Value())
                .primaryKey(entry.getPrimaryKey())
                .build();
    }

    private CompletableFuture<Long> saveAll(List<AuditLogEntry> entries) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Saving log entries to index from:{}, to:{}...START", entries.get(0).getEntryNo(), entries.get(entries.size() - 1).getEntryNo());
            List<AuditLogDoc> logs = entries.stream()
                    .map(AuditLogImportJob::fromAuditLogEntry)
                    .collect(Collectors.toList());
            Iterable<AuditLogDoc> saved = systemAuthenticator.withSystem(() -> {
                try {
                    return auditLogDocRepository.saveAll(logs);
                } catch (Exception ex) {
                    log.error("Error occurred while saving records from entryNo: {} - to entryNo: {}",
                            entries.get(0).getEntryNo(), entries.get(entries.size() -1).getEntryNo(), ex);
                }
                return Collections.emptyList();
            });
            log.info("Saved log entries to index from:{}, to:{}...DONE", entries.get(0).getEntryNo(), entries.get(entries.size() - 1).getEntryNo());
            return StreamSupport.stream(saved
                    .spliterator(), false).count();
        });
    }

    private AuditLogEntries fetchAuditLogEntries(boolean count, int batchSize, long skip) {
        return retryTemplate.execute(retryContext -> auditLogEntriesService.getAuditLogEntries(count, batchSize, skip > 0 ? Optional.of(skip) : Optional.empty()));
    }

}
