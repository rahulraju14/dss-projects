package com.dss.auditlog.rest;

import com.dss.auditlog.rest.service.AuditLogEntriesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestConfig {
    private final BusinessCentralProperties businessCentralProperties;
    private final OAuthTokenInterceptor oAuthTokenInterceptor;

    public RestConfig(BusinessCentralProperties businessCentralProperties, OAuthTokenInterceptor oAuthTokenInterceptor) {
        this.businessCentralProperties = businessCentralProperties;
        this.oAuthTokenInterceptor = oAuthTokenInterceptor;
    }


    @Bean
    RestClient restClient(ObjectMapper objectMapper) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(HttpClient
                .newBuilder()
                .connectTimeout(Duration.ofMinutes(5))
                .build()
        );
        requestFactory.setReadTimeout(Duration.ofMinutes(5));
        RestClient restClient = RestClient.builder()
                .baseUrl(businessCentralProperties.getUrl())
                .requestFactory(requestFactory)
                .requestInterceptor(oAuthTokenInterceptor)
                .build();
        return restClient;
    }

    @SneakyThrows
    @Bean
    AuditLogEntriesService auditLogEntriesService(RestClient restClient) {
        HttpServiceProxyFactory httpServiceProxyFactory =
                HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                        .build();
        return httpServiceProxyFactory.createClient(AuditLogEntriesService.class);
    }

}
