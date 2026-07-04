package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.TicketCategory;
import com.pia.ticketmanagement.model.TicketSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketSubCategoryRepository extends JpaRepository<TicketSubCategory, Long> {

    boolean existsByNameAndCategory(String name, TicketCategory category);


}