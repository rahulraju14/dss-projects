package com.dss.auditlog.elasticsearch.repository;

import com.dss.auditlog.elasticsearch.documents.AuditLogDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AuditLogDocRepository extends ElasticsearchRepository<AuditLogDoc, Long> {
}
