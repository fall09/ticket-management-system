package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}