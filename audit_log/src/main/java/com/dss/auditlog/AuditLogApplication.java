package com.dss.auditlog;

import com.dss.auditlog.elasticsearch.documents.AuditLogDoc;
import com.dss.auditlog.elasticsearch.repository.AuditLogDocRepository;
import com.dss.auditlog.elasticsearch.repository.AuditLogSearchRepository;
import com.dss.auditlog.jobs.AuditLogImportJob;
import com.dss.auditlog.rest.dto.AuditLogEntries;
import com.dss.auditlog.rest.dto.AuditLogEntry;
import com.dss.auditlog.rest.service.AuditLogEntriesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Strings;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import io.jmix.core.security.SystemAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Push
@Theme(value = "AuditLog")
@PWA(name = "AuditLog", shortName = "AuditLog")
@SpringBootApplication
@EnableRetry
@Slf4j
public class AuditLogApplication implements AppShellConfigurator, CommandLineRunner {

    @Autowired
    private Environment environment;

    @Autowired
    @Lazy
    private AuditLogEntriesService auditLogEntriesService;

    @Autowired
    private RetryTemplate retryTemplate;
    @Autowired
    @Lazy
    private SystemAuthenticator systemAuthenticator;
    @Autowired
    private AuditLogDocRepository auditLogDocRepository;

    public static void main(String[] args) {
        SpringApplication.run(AuditLogApplication.class, args);
    }

    @Bean
    @Primary
    @ConfigurationProperties("main.datasource")
    DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("main.datasource.hikari")
    DataSource dataSource(final DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @EventListener
    public void printApplicationUrl(final ApplicationStartedEvent event) {
        LoggerFactory.getLogger(AuditLogApplication.class).info("Application started at "
                + "http://localhost:"
                + environment.getProperty("local.server.port")
                + Strings.nullToEmpty(environment.getProperty("server.servlet.context-path")));
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("AuditLogImportJob...START");
            final int batchSize = 1000;
            boolean hasMore = false;
            long skip = 0l;
            List<CompletableFuture<Long>> batches = new ArrayList<>();
            do {
                log.info("Fetching AuditLog Entries.. skip:{}", skip);
                AuditLogEntries result = fetchAuditLogEntries(false, batchSize, skip);
                List<AuditLogEntry> entries = result.getEntries();
                hasMore = entries.size() == batchSize;
                if (hasMore) {
                    skip = entries.get(entries.size() - 1).getEntryNo();
                }
                if (!entries.isEmpty()) {
                    log.info("Fetched AuditLog Entries.. from:{}, to:{}", entries.get(0).getEntryNo(), entries.get(entries.size() - 1).getEntryNo());
                    CompletableFuture<Long> batch = saveAll(entries);
                    batches.add(batch);
                }
            } while (hasMore);
            Long count = batches.stream()
                    .map(CompletableFuture::join)
                    .reduce(0L, Long::sum);
            log.info("Total {} Audi logs inserted", count);

            log.info("AuditLogImportJob...END");
        } catch (Exception ex) {
            log.error("Error while importing records: {}", ex);
            throw new RuntimeException(ex);
        }
    }

    private AuditLogEntries fetchAuditLogEntries(boolean count, int batchSize, long skip) {
        return retryTemplate.execute(retryContext -> auditLogEntriesService.getAuditLogEntriesByDocumentType(count, batchSize, skip > 0 ? Optional.of(skip) : Optional.empty()));
    }

    private CompletableFuture<Long> saveAll(List<AuditLogEntry> entries) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Saving log entries to index from:{}, to:{}...START", entries.get(0).getEntryNo(), entries.get(entries.size() - 1).getEntryNo());
            List<AuditLogDoc> logs = entries.stream()
                    .map(AuditLogImportJob::fromAuditLogEntry)
                    .collect(Collectors.toList());
            Iterable<AuditLogDoc> saved = systemAuthenticator.withSystem(() -> {
                try {
                    return auditLogDocRepository.saveAll(logs);
                } catch (Exception ex) {
                    log.error("Error occurred while saving records from entryNo: {} - to entryNo: {}",
                            entries.get(0).getEntryNo(), entries.get(entries.size() -1).getEntryNo(), ex);
                }
                return Collections.emptyList();
            });
            log.info("Saved log entries to index from:{}, to:{}...DONE", entries.get(0).getEntryNo(), entries.get(entries.size() - 1).getEntryNo());
            return StreamSupport.stream(saved
                    .spliterator(), false).count();
        });
    }

}
