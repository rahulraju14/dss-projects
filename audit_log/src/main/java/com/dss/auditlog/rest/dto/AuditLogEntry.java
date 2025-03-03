package com.dss.auditlog.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntry {
    @JsonProperty("Entry_No")
    private Long entryNo;
    @JsonProperty("System_Created_By_USER_ID")
    private String createdBy;
    @JsonProperty("Table_Caption")
    private String tableCaption;
    @JsonProperty("Table_No")
    private Integer tableNo;
    @JsonProperty("Document_Type")
    private String documentType;
    @JsonProperty("Document_No")
    private String documentNo;
    @JsonProperty("Document_Created_Date__x0026__Time")
    private String createdDate;
    @JsonProperty("Field_CaptionCustom")
    private String fieldCaption;
    @JsonProperty("Type_of_Change")
    private String typeOfChange;
    @JsonProperty("Old_Value")
    private String oldValue;
    @JsonProperty("New_Value")
    private String newValue;
    @JsonProperty("Reference_Description")
    private String referenceDescription;
    @JsonProperty("Primary_Key_Field_1_Caption")
    private String primaryKeyField1Description;
    @JsonProperty("Primary_Key_Field_1_Value")
    private String primaryKeyField1Value;
    @JsonProperty("Primary_Key_Field_2_Caption")
    private String primaryKeyField2Description;
    @JsonProperty("Primary_Key_Field_2_Value")
    private String primaryKeyField2Value;
    @JsonProperty("Primary_Key_Field_3_Caption")
    private String primaryKeyField3Description;
    @JsonProperty("Primary_Key_Field_3_Value")
    private String primaryKeyField3Value;
    @JsonProperty("Primary_Key")
    private String primaryKey;
    @JsonProperty("Exported_to_Kafka")
    private boolean exported;
}
