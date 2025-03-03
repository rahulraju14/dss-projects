package com.dss.auditlog.excel;

import io.jmix.flowui.data.DataUnit;

public interface CustomEntitiesLoader {
    interface EntityVisitor {
        boolean visitEntity(CustomEntityExportContext entityExportContext);
    }

    String getPaginationStrategy();

    void loadAll(DataUnit dataUnit, EntityVisitor exportedEntityVisitor);
}
