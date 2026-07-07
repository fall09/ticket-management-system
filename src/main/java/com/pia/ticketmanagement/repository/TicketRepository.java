package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.CustomerStatusHistory;
import com.pia.ticketmanagement.model.Ticket;
import com.pia.ticketmanagement.model.TicketPriority;
import com.pia.ticketmanagement.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    long countByCustomerId(Long customerId);

    @Query("""
SELECT t FROM Ticket t
WHERE (:status IS NULL OR t.status = :status)
AND (:priority IS NULL OR t.priority = :priority)
AND (
  :search IS NULL OR
  LOWER(t.ticketNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
  LOWER(t.customer.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR
  LOWER(t.customer.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR
  t.customer.phoneNumber LIKE CONCAT('%', :search, '%') OR
  LOWER(t.category.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
  LOWER(t.subCategory.name) LIKE LOWER(CONCAT('%', :search, '%'))
)
""")
    List<Ticket> filterTickets(
            @Param("search") String search,
            @Param("status") TicketStatus status,
            @Param("priority") TicketPriority priority
    );

    long countByAssignedEmployeeId(Long employeeId);



}