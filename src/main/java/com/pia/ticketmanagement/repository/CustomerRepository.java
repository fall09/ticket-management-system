package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    List<Customer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrPhoneNumberContaining(

            String firstName,

            String lastName,

            String phoneNumber

    );
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByEmail(String email);
    @Query("""

            SELECT c FROM Customer c

            WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%'))

               OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%'))

               OR LOWER(CONCAT(c.firstName, ' ', c.lastName)) LIKE LOWER(CONCAT('%', :search, '%'))

               OR c.phoneNumber LIKE CONCAT('%', :search, '%')

               OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))

            """)

    List<Customer> searchCustomers(@Param("search") String search);

}