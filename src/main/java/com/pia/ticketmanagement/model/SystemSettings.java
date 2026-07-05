package com.pia.ticketmanagement.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSettings {

    @Id
    private Long id;

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