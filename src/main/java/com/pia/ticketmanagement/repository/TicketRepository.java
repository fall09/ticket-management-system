package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.CustomerStatusHistory;
import com.pia.ticketmanagement.model.Ticket;
import com.pia.ticketmanagement.model.TicketPriority;
import com.pia.ticketmanagement.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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
    List<Ticket> findByAssignedEmployeeIsNull();

    List<Ticket> findByAssignedEmployeeId(Long employeeId);

    @Query("""
SELECT t.category.name, COUNT(t)
FROM Ticket t
GROUP BY t.category.name
ORDER BY COUNT(t) DESC, t.category.name ASC
""")
    List<Object[]> getCategoryDistribution();

    @Query("""
SELECT t.issueProvince.name, COUNT(t)
FROM Ticket t
GROUP BY t.issueProvince.name
ORDER BY COUNT(t) DESC, t.issueProvince.name ASC
""")
    List<Object[]> getProvinceDistribution();

    @Query("""
SELECT t.priority, COUNT(t)
FROM Ticket t
GROUP BY t.priority
ORDER BY COUNT(t) DESC
""")
    List<Object[]> getPriorityDistribution();

    @Query("""
SELECT t.subCategory.name, COUNT(t)
FROM Ticket t
GROUP BY t.subCategory.name
ORDER BY COUNT(t) DESC, t.subCategory.name ASC
""")
    List<Object[]> getSubCategoryDistribution();

    @Query("""
SELECT FUNCTION('DATE', t.createdAt), COUNT(t),
       SUM(CASE WHEN t.status = com.pia.ticketmanagement.model.TicketStatus.RESOLVED OR t.status = com.pia.ticketmanagement.model.TicketStatus.CLOSED THEN 1 ELSE 0 END)
FROM Ticket t
GROUP BY FUNCTION('DATE', t.createdAt)
ORDER BY FUNCTION('DATE', t.createdAt)
""")
    List<Object[]> getDailyTrend();
}