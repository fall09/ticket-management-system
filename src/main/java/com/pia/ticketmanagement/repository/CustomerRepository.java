package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.Customer;
import com.pia.ticketmanagement.model.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByEmail(String email);

    @Query(
            value = """
                SELECT c.*
                FROM customers c
                LEFT JOIN tickets t ON t.customer_id = c.id
                WHERE (:status IS NULL OR c.status = CAST(:status AS varchar))
                AND (:provinceId IS NULL OR c.province_id = :provinceId)
                AND (:districtId IS NULL OR c.district_id = :districtId)
                AND (
                    :search IS NULL OR
                    c.first_name ILIKE CONCAT('%', CAST(:search AS varchar), '%') OR
                    c.last_name ILIKE CONCAT('%', CAST(:search AS varchar), '%') OR
                    CONCAT(c.first_name, ' ', c.last_name) ILIKE CONCAT('%', CAST(:search AS varchar), '%') OR
                    c.phone_number ILIKE CONCAT('%', CAST(:search AS varchar), '%') OR
                    c.email ILIKE CONCAT('%', CAST(:search AS varchar), '%')
                )
                GROUP BY c.id
                HAVING (
                    :hasTicket IS NULL
                    OR (:hasTicket = true AND COUNT(t.id) > 0)
                    OR (:hasTicket = false AND COUNT(t.id) = 0)
                )
                """,
            countQuery = """
                SELECT COUNT(*)
                FROM (
                    SELECT c.id
                    FROM customers c
                    LEFT JOIN tickets t ON t.customer_id = c.id
                    WHERE (:status IS NULL OR c.status = CAST(:status AS varchar))
                    AND (:provinceId IS NULL OR c.province_id = :provinceId)
                    AND (:districtId IS NULL OR c.district_id = :districtId)
                    AND (
                        :search IS NULL OR
                        c.first_name ILIKE CONCAT('%', CAST(:search AS varchar), '%') OR
                        c.last_name ILIKE CONCAT('%', CAST(:search AS varchar), '%') OR
                        CONCAT(c.first_name, ' ', c.last_name) ILIKE CONCAT('%', CAST(:search AS varchar), '%') OR
                        c.phone_number ILIKE CONCAT('%', CAST(:search AS varchar), '%') OR
                        c.email ILIKE CONCAT('%', CAST(:search AS varchar), '%')
                    )
                    GROUP BY c.id
                    HAVING (
                        :hasTicket IS NULL
                        OR (:hasTicket = true AND COUNT(t.id) > 0)
                        OR (:hasTicket = false AND COUNT(t.id) = 0)
                    )
                ) filtered_customers
                """,
            nativeQuery = true
    )
    Page<Customer> filterCustomers(
            @Param("search") String search,
            @Param("status") String status,
            @Param("provinceId") Long provinceId,
            @Param("districtId") Long districtId,
            @Param("hasTicket") Boolean hasTicket,
            Pageable pageable
    );

    @Query("""
SELECT
COUNT(c),
SUM(CASE WHEN c.status = 'ACTIVE' THEN 1 ELSE 0 END),
SUM(CASE WHEN c.status = 'INACTIVE' THEN 1 ELSE 0 END),
SUM(CASE WHEN c.status = 'SUSPENDED' THEN 1 ELSE 0 END),
SUM(CASE WHEN c.status = 'POTENTIAL' THEN 1 ELSE 0 END)
FROM Customer c
""")
    java.util.List<Object[]> getCustomerStatistics();

    long countByStatus(CustomerStatus status);
}