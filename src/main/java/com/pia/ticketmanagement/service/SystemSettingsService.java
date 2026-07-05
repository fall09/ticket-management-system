package com.pia.ticketmanagement.service;

import com.pia.ticketmanagement.dto.request.SystemSettingsRequest;
import com.pia.ticketmanagement.dto.response.SystemSettingsResponse;
import com.pia.ticketmanagement.model.SystemSettings;
import com.pia.ticketmanagement.repository.SystemSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SystemSettingsService {

    private final SystemSettingsRepository repository;

    public SystemSettingsResponse getSettings() {
        return mapToResponse(getOrCreateSettings());
    }

    public SystemSettingsResponse updateSettings(SystemSettingsRequest request) {
        SystemSettings settings = getOrCreateSettings();

        settings.setVpnRequired(request.isVpnRequired());
        settings.setReadOnlyAgents(request.isReadOnlyAgents());
        settings.setSessionTimeout(request.getSessionTimeout());
        settings.setDefaultRole(request.getDefaultRole());

        settings.setDefaultPriority(request.getDefaultPriority());
        settings.setDefaultStatus(request.getDefaultStatus());
        settings.setLocationRequiredDefault(request.isLocationRequiredDefault());
        settings.setAutoAssignTickets(request.isAutoAssignTickets());

        settings.setEmailNotifications(request.isEmailNotifications());
        settings.setCriticalTicketAlerts(request.isCriticalTicketAlerts());
        settings.setSuspendedCustomerAlerts(request.isSuspendedCustomerAlerts());

        settings.setTrackStatusChanges(request.isTrackStatusChanges());
        settings.setTrackCategoryChanges(request.isTrackCategoryChanges());

        return mapToResponse(repository.save(settings));
    }

    private SystemSettings getOrCreateSettings() {
        return repository.findById(1L).orElseGet(() ->
                repository.save(SystemSettings.builder()
                        .id(1L)
                        .vpnRequired(true)
                        .readOnlyAgents(true)
                        .sessionTimeout("60")
                        .defaultRole("AGENT")
                        .defaultPriority("MEDIUM")
                        .defaultStatus("OPEN")
                        .locationRequiredDefault(true)
                        .autoAssignTickets(false)
                        .emailNotifications(true)
                        .criticalTicketAlerts(true)
                        .suspendedCustomerAlerts(true)
                        .trackStatusChanges(true)
                        .trackCategoryChanges(true)
                        .build())
        );
    }

    private SystemSettingsResponse mapToResponse(SystemSettings settings) {
        return SystemSettingsResponse.builder()
                .vpnRequired(settings.isVpnRequired())
                .readOnlyAgents(settings.isReadOnlyAgents())
                .sessionTimeout(settings.getSessionTimeout())
                .defaultRole(settings.getDefaultRole())
                .defaultPriority(settings.getDefaultPriority())
                .defaultStatus(settings.getDefaultStatus())
                .locationRequiredDefault(settings.isLocationRequiredDefault())
                .autoAssignTickets(settings.isAutoAssignTickets())
                .emailNotifications(settings.isEmailNotifications())
                .criticalTicketAlerts(settings.isCriticalTicketAlerts())
                .suspendedCustomerAlerts(settings.isSuspendedCustomerAlerts())
                .trackStatusChanges(settings.isTrackStatusChanges())
                .trackCategoryChanges(settings.isTrackCategoryChanges())
                .build();
    }
}