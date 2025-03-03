package com.dss.auditlog.utils;

import io.jmix.core.metamodel.annotation.JmixEntity;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
public class DashboardDataHolder {
    private String tableEntry;
    private Long modificationCount = 0L;
    private Long DeletionCount = 0L;
    private Long totalCount = 0L;
}
