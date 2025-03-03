package com.dss.auditlog.excel;

import io.jmix.gridexportflowui.GridExportProperties;
import io.jmix.gridexportflowui.exporter.entitiesloader.AllEntitiesLoader;
import io.jmix.gridexportflowui.exporter.entitiesloader.AllEntitiesLoaderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CustomEntitiesLoaderFactory extends AllEntitiesLoaderFactory {

    private final CustomKeySetEntityLoader entityLoader;

    public CustomEntitiesLoaderFactory(GridExportProperties gridExportProperties, List<AllEntitiesLoader> allEntitiesLoaders, CustomKeySetEntityLoader entityLoader) {
        super(gridExportProperties, allEntitiesLoaders);
        this.entityLoader = entityLoader;
    }

    @Override
    public AllEntitiesLoader getEntitiesLoader() {
        return entityLoader;
    }
}
