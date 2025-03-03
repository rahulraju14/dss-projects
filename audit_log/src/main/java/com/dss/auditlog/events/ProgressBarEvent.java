package com.dss.auditlog.events;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.ApplicationEvent;

@Getter
public class ProgressBarEvent extends ApplicationEvent {

    private final String progressId;
    private final Double progressValue;
    private final boolean taskCompleted;
    private final Workbook result;
    private final String fileName;
    private final String contentType;
    private final boolean errorOccurred;


    public ProgressBarEvent(Object source, String progressId, Double progressValue, boolean taskCompleted, Workbook result, String fileName, String contentType, boolean errorOccurred) {
        super(source);
        this.progressId = progressId;
        this.progressValue = progressValue;
        this.taskCompleted = taskCompleted;
        this.result = result;
        this.fileName = fileName;
        this.contentType = contentType;
        this.errorOccurred = errorOccurred;
    }
}
