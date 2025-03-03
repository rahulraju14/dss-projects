package com.dss.auditlog.rest.service;

import com.dss.auditlog.rest.dto.AuditLogEntries;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.Optional;

@HttpExchange(url = "", accept = "application/json", contentType = "application/json")
public interface AuditLogEntriesService {

    @GetExchange("/AuditLogEntries")
    AuditLogEntries getAuditLogEntries(
            @RequestParam("$count") boolean count,
            @RequestParam("$top") Integer top,
            @RequestParam("$skiptoken")Optional<Long> skipToken
            );

    @GetExchange("/SalesInvoiceFilterOnAuditLE")
    AuditLogEntries getAuditLogEntriesByDocumentType(
            @RequestParam("$count") boolean count,
            @RequestParam("$top") Integer top, @RequestParam("$skiptoken")Optional<Long> skipToken);
}
