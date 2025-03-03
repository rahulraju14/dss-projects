package com.dss.auditlog.elasticsearch;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.RestClients;
import org.opensearch.spring.boot.autoconfigure.OpenSearchProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.dss.auditlog.elasticsearch")
//@EnableAutoConfiguration(exclude={ElasticsearchDataAutoConfiguration.class, ElasticsearchRestClientAutoConfiguration.class})
public class AuditLogOpenSearchConfiguration extends AbstractOpenSearchConfiguration {
    @Value("${opensearch.ssl:false}")
    private boolean ssl;

    private final OpenSearchProperties properties;

    public AuditLogOpenSearchConfiguration(OpenSearchProperties openSearchProperties) {
        this.properties = openSearchProperties;
    }

    @Override
    @Bean
    public RestHighLevelClient opensearchClient() {
        System.setProperty("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jdk-21\\lib\\security\\cacerts");
        ClientConfiguration clientConfiguration = null;
        try {
            clientConfiguration = createClientConfigurationBuilder(ssl)
                    .withBasicAuth(properties.getUsername(), properties.getPassword())
                    .withConnectTimeout(Duration.ofMinutes(20L))
                    .withSocketTimeout(Duration.ofMinutes(20L))
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }


        return RestClients.create(clientConfiguration)
                .rest();

    }

    private ClientConfiguration.TerminalClientConfigurationBuilder createClientConfigurationBuilder(boolean ssl) throws NoSuchAlgorithmException {
        ClientConfiguration.MaybeSecureClientConfigurationBuilder builder = ClientConfiguration.builder().connectedTo(properties.getUris().toArray(new String[properties.getUris().size()]));
        if (ssl) {
            return builder.usingSsl(SSLContext.getDefault(), NoopHostnameVerifier.INSTANCE);
        }
        return builder;
    }
}
