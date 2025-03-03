package com.dss.auditlog.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RecordCountEvent extends ApplicationEvent {
    private final int totalRecords;
    public RecordCountEvent(Object source, int totalRecords) {
        super(source);
        this.totalRecords = totalRecords;
    }
}
