package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {
}