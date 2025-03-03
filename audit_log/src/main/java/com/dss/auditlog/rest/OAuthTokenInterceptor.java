package com.dss.auditlog.rest;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
public class OAuthTokenInterceptor implements ClientHttpRequestInterceptor {
    private OAuth2AccessToken token;
    private final BusinessCentralProperties properties;

    public OAuthTokenInterceptor(BusinessCentralProperties properties) {
        this.properties = properties;
    }

    @NotNull
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().setBearerAuth(getToken().getValue());
        return execution.execute(request, body);
    }


    public OAuth2AccessToken getToken() {
        if (token != null) {
            if (token.isExpired()) {
                token = null;
            }
        }
        if (token == null) {
            RestTemplate simpleRestTemplate = new RestTemplateBuilder().
                    build();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", properties.getOauth2().getGrantType());
            map.add("client_id", properties.getOauth2().getClientId());
            map.add("client_secret", properties.getOauth2().getClientSecret());
            map.add("scope", properties.getOauth2().getScope());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            token = simpleRestTemplate.postForObject(
                    properties.getOauth2().getUrl(),
                    request,
                    OAuth2AccessToken.class
            );
        }
        return token;
    }
}
