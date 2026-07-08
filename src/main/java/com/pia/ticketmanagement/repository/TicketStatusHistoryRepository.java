package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.TicketStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketStatusHistoryRepository extends JpaRepository<TicketStatusHistory, Long> {

    List<TicketStatusHistory> findByTicketIdOrderByChangedAtDesc(Long ticketId);
}