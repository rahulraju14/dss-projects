package com.dss.auditlog.security;

import com.dss.auditlog.elasticsearch.documents.AuditLogDoc;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.role.annotation.SpecificPolicy;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "UI:auditor", code = AuditorAccessRole.CODE, scope = "UI")
public interface AuditorAccessRole {

    String CODE = "auditor";
    @SpecificPolicy(resources = "ui.loginToUi")
    void loginAccess();
    @ViewPolicy(viewIds = "AL_AuditLogDoc.list")
    void dashBoardAccess();

    @MenuPolicy(menuIds = "AL_AuditLogDoc.list")
    void menuAccess();
    @EntityPolicy(entityClass = AuditLogDoc.class, actions = EntityPolicyAction.ALL)
    @EntityAttributePolicy(entityClass = AuditLogDoc.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    void entityAccess();

}
