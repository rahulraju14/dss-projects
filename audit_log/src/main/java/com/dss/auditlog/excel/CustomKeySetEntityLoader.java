package com.dss.auditlog.excel;

import com.dss.auditlog.elasticsearch.documents.AuditLogDoc;
import com.dss.auditlog.elasticsearch.repository.AuditLogSearchRepository;
import com.dss.auditlog.utils.ExcelRowTracker;
import io.jmix.core.DataManager;
import io.jmix.core.LoadContext;
import io.jmix.core.MetadataTools;
import io.jmix.core.common.util.Preconditions;
import io.jmix.flowui.backgroundtask.TaskLifeCycle;
import io.jmix.flowui.data.DataUnit;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.DataLoader;
import io.jmix.flowui.model.KeyValueCollectionLoader;
import io.jmix.gridexportflowui.GridExportProperties;
import io.jmix.gridexportflowui.exporter.EntityExportContext;
import io.jmix.gridexportflowui.exporter.entitiesloader.KeysetAllEntitiesLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitsIterator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CustomKeySetEntityLoader extends KeysetAllEntitiesLoader {
    public static final String TOTAL_COUNT = "TOTAL_COUNT";

    @Autowired
    private AuditLogSearchRepository auditLogSearchRepository;

    public CustomKeySetEntityLoader(MetadataTools metadataTools, DataManager dataManager, PlatformTransactionManager platformTransactionManager,
                                    GridExportProperties gridExportProperties) {
        super(metadataTools, dataManager, platformTransactionManager, gridExportProperties);
    }

    public void customLoadAll(DataUnit dataUnit, CustomEntitiesLoader.EntityVisitor exportedEntityVisitor, TaskLifeCycle<Double> taskLifeCycle, LocalDate from, LocalDate to, ExcelRowTracker counter) {
        Preconditions.checkNotNullArgument(exportedEntityVisitor,
                "Cannot export all rows. DataUnit can't be null");
        Preconditions.checkNotNullArgument(exportedEntityVisitor,
                "Cannot export all rows. Entity exporter can't be null");

        DataLoader dataLoader = getDataLoader(dataUnit);
        int loadBatchSize = gridExportProperties.getExportAllBatchSize();

        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            if (dataLoader instanceof CollectionLoader<?> collectionLoader) {
                customLoadEntities(collectionLoader, exportedEntityVisitor, loadBatchSize, taskLifeCycle, from, to, counter);
            }else {
                throw new IllegalArgumentException("Cannot export all rows. Loader type is not supported.");
            }
        });
    }

    public void customLoadEntities(CollectionLoader<?> collectionLoader, CustomEntitiesLoader.EntityVisitor exportedEntityVisitor, int loadBatchSize, TaskLifeCycle<Double> taskLifeCycle, LocalDate from, LocalDate to, ExcelRowTracker counter) {
        log.info("--- Custom load entities invoked ---");
        int count = 0;

        StopWatch stopWatch = new StopWatch();
        LoadContext<AuditLogDoc> loadContext = generateLoadContext(collectionLoader);

        try {
            SearchHitsIterator<AuditLogDoc> result = auditLogSearchRepository.getAuditLogScrollingData(loadContext, from, to, loadBatchSize);
            stopWatch.start();
            long totalRecords = result.getTotalHits();
            updateProgress(count, totalRecords, taskLifeCycle);

            CustomEntityExportContext customEntityExportContext = new CustomEntityExportContext();
            while (!Thread.currentThread().isInterrupted() && result.hasNext()) {
                List<AuditLogDoc> auditLogRecords = result.stream().filter(f -> !Thread.currentThread().isInterrupted())
                        .map(SearchHit::getContent)
                        .limit(loadBatchSize)
                        .collect(Collectors.toList());

                for (Object entity : auditLogRecords) {
                    counter.incrementRowCount();
                    customEntityExportContext.setAuditLogEntity(entity);
                    customEntityExportContext.setRowNumber(counter.getRowIndex());
                    exportedEntityVisitor.visitEntity(customEntityExportContext);
                }

                log.info("-- updated row count from customKeySetEntityLoader: {}", counter.getRowIndex());
                int auditLogListSize = auditLogRecords.size();
                log.info("Fetched AuditLog Entries.. from: {}, to: {} | size: {}",
                        auditLogRecords.get(0).getEntryNo(), auditLogRecords.get(auditLogListSize - 1).getEntryNo(), auditLogListSize);

                count += auditLogListSize;
                updateProgress(count, totalRecords, taskLifeCycle);

                if (auditLogListSize < loadBatchSize) {
                    break;
                }
            }

            stopWatch.stop();
            log.info("Total records fetched : {} | in {}ms", count, stopWatch.getTotalTimeMillis());
        } catch (InterruptedException io) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(io);
        } catch (Exception ex) {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            throw new RuntimeException(ex);
        }
    }

    private void updateProgress(int progressCount, long totalRecords, TaskLifeCycle<Double> taskLifeCycle) throws InterruptedException {
        double progress = ((double) progressCount / totalRecords);
        taskLifeCycle.publish(progress);
    }
}
