package com.dss.auditlog.events;

import io.jmix.flowui.backgroundtask.BackgroundTaskHandler;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event responsible for terminating AuditLog excel report task in UI thread
 */
@Getter
public class BackgroundTaskEvent extends ApplicationEvent {

    private final String progressId;
    private final BackgroundTaskHandler backgroundTaskHandler;

    public BackgroundTaskEvent(Object source, String progressId, BackgroundTaskHandler backgroundTaskHandler) {
        super(source);
        this.progressId = progressId;
        this.backgroundTaskHandler = backgroundTaskHandler;
    }
}
