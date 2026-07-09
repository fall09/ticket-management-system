package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.TicketActivityLog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketActivityLogRepository extends JpaRepository<TicketActivityLog, Long> {

    List<TicketActivityLog> findByTicketIdOrderByCreatedAtDesc(Long ticketId);


}