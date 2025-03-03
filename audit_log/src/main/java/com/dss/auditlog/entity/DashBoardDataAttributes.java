package com.dss.auditlog.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashBoardDataAttributes {
    private String colKey;
    private String filterValue;
    private String fromDate;
    private String toDate;
    private Map<String, String> propertyFilterMap;
    private DashBoardComponentType componentType;

    public enum DashBoardComponentType {
        PIE_CHART,
        BAR_CHART,
        USER_TABLE,
        TABLE_CAPTION
    }

}
