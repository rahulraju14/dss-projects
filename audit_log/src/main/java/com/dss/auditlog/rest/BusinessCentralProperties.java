package com.dss.auditlog.rest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "audit-log.business-central")
public class BusinessCentralProperties {
    private String url;
    private OAuth2 oauth2;


    @Getter
    @Setter
    public static class OAuth2{
        private String url;
        private String grantType;
        private String clientId;
        private String clientSecret;
        private String scope;
    }
}
