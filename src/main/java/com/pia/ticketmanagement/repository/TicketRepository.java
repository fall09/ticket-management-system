package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.CustomerStatusHistory;
import com.pia.ticketmanagement.model.Ticket;
import com.pia.ticketmanagement.model.TicketPriority;
import com.pia.ticketmanagement.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    boolean existsByTicketNumber(String ticketNumber);

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByPriority(TicketPriority priority);

    List<Ticket> findByCustomerId(Long customerId);

    List<Ticket> findByCategoryId(Long categoryId);

    List<Ticket> findBySubCategoryId(Long subCategoryId);
}