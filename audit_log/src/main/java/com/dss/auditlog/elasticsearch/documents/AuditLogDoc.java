package com.dss.auditlog.elasticsearch.documents;

import io.jmix.core.entity.annotation.JmixId;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@FieldNameConstants
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JmixEntity(name = "AL_AuditLogDoc")
@EqualsAndHashCode(of = "entryNo")
@Document(indexName = "#{@environment.getProperty('audit-log.elasticsearch.index.audit-log.name')}", createIndex = true)
public class AuditLogDoc implements Persistable<Long> {
    @Id
    @JmixId
    @InstanceName
    private Long entryNo;

    private String createdBy;

    private String tableCaption;

    private Integer tableNo;

    private String documentType;

    private String documentNo;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private OffsetDateTime createdDate;

    private String fieldCaption;

    @Field(type = FieldType.Keyword)
    private String typeOfChange;

    private String oldValue;

    private String newValue;

    private String referenceDescription;

    private String primaryKeyField1Description;

    private String primaryKeyField1Value;

    private String primaryKeyField2Description;

    private String primaryKeyField2Value;

    private String primaryKeyField3Description;

    private String primaryKeyField3Value;

    private String primaryKey;

    @CreatedDate
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime systemCreatedDate;

    @CreatedBy
    private String systemCreatedBy;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    @LastModifiedDate
    private LocalDateTime systemLastModifiedDate;

    @LastModifiedBy
    private String systemLastModifiedBy;

    @Override
    public Long getId() {
        return entryNo;
    }

    @Override
    public boolean isNew() {
        return getId() == null || (systemCreatedBy == null && systemCreatedDate == null);
    }
}