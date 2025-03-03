package com.dss.auditlog.excel;

import io.jmix.core.DataManager;
import io.jmix.core.LoadContext;
import io.jmix.core.MetadataTools;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.gridexportflowui.GridExportProperties;
import io.jmix.gridexportflowui.exporter.EntityExportContext;
//import io.jmix.gridexportflowui.exporter.excel.ExcelAllRecordsExporter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Component("AL_ExcelAllRecordsExporter")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AuditLogExcelAllRecordsExporter {
    private CollectionLoader dataLoader;

    public AuditLogExcelAllRecordsExporter(MetadataTools metadataTools, DataManager dataManager, PlatformTransactionManager platformTransactionManager, GridExportProperties gridExportProperties) {
//        super(metadataTools, dataManager, platformTransactionManager, gridExportProperties);
    }

    public CollectionLoader getDataLoader() {
        return dataLoader;
    }

    public void setDataLoader(CollectionLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

//    @Override
    protected void exportEntities(CollectionLoader<?> collectionLoader,
                                  Predicate<EntityExportContext> entityExporter,
                                  int loadBatchSize) {
        int rowNumber = 0;
        int firstResult = 0;
        boolean proceedToExport = true;
        boolean lastBatchLoaded = false;

        while (!lastBatchLoaded && proceedToExport) {
            LoadContext loadContext = collectionLoader.createLoadContext();
            //query is not null - checked when generated load context
            LoadContext.Query query = Objects.requireNonNull(loadContext.getQuery());
            query.setFirstResult(firstResult);
            query.setMaxResults(loadBatchSize);
            query.setSort(null);
            List<?> entities = collectionLoader.getLoadDelegate().apply(loadContext);
            for (Object entity : entities) {
                EntityExportContext entityExportContext = new EntityExportContext(entity, ++rowNumber);
                proceedToExport = entityExporter.test(entityExportContext);
                if (!proceedToExport) {
                    break;
                }
            }

            int loadedEntitiesAmount = entities.size();
            if (loadedEntitiesAmount > 0) {
                firstResult += loadedEntitiesAmount;
            }
            lastBatchLoaded = loadedEntitiesAmount == 0 || loadedEntitiesAmount < loadBatchSize;
        }
    }


}
