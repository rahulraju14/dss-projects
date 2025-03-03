package com.dss.auditlog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

@Slf4j
public class RetryCustomListener implements RetryListener {
    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        log.info("Retrying service due to exception: {} attempting : {}..", throwable.getMessage(), context.getRetryCount());
    }
}
