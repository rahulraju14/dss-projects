package com.example.test;

import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

public class CustomRetryTemplate extends RestTemplate{
    private final RetryTemplate retryTemplate;

    public CustomRetryTemplate(RetryTemplate template) {
        this.retryTemplate = template;
    }
    @Override
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Object... uriVariables) throws RestClientException {
        return retryTemplate.execute(retryContex -> super.postForEntity(url, request, responseType, uriVariables));
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        return super.postForEntity(url, request, responseType, uriVariables);
    }

    @Override
    public <T> ResponseEntity<T> postForEntity(URI url, Object request, Class<T> responseType) throws RestClientException {
        return super.postForEntity(url, request, responseType);
    }
}
