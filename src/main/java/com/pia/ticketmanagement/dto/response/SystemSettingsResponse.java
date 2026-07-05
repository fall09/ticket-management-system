package com.pia.ticketmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemSettingsResponse {
    private boolean vpnRequired;
    private boolean readOnlyAgents;
    private String sessionTimeout;
    private String defaultRole;

    private String defaultPriority;
    private String defaultStatus;
    private boolean locationRequiredDefault;
    private boolean autoAssignTickets;

    private boolean emailNotifications;
    private boolean criticalTicketAlerts;
    private boolean suspendedCustomerAlerts;

    private boolean trackStatusChanges;
    private boolean trackCategoryChanges;
}