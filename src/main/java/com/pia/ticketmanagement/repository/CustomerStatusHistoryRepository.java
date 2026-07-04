package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.CustomerStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerStatusHistoryRepository extends JpaRepository<CustomerStatusHistory, Long> {

    List<CustomerStatusHistory> findByCustomerIdOrderByChangedAtDesc(Long customerId);
}