package com.dss.auditlog.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum DateRange implements EnumClass<Integer> {

    TODAY(1),
    CURRENT_WEEK(2),
    LAST_WEEK(3),
    CURRENT_MONTH(4),
    LAST_MONTH(5),
    CURRENT_FY_YEAR(6),
    CUSTOM(7);

    private final Integer id;

    DateRange(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static DateRange fromId(Integer id) {
        for (DateRange at : DateRange.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}