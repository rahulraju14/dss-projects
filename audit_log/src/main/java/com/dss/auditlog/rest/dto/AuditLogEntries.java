package com.dss.auditlog.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntries {
    @JsonProperty("@odata.count")
    private Integer count;
    @JsonProperty("value")
    private List<AuditLogEntry> entries;
}
