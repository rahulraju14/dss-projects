package com.dss.auditlog.excel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@NoArgsConstructor
public class CustomEntityExportContext {
    private Object auditLogEntity;
    private int rowNumber;
}
