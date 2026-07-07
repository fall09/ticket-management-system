package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.TicketCategory;
import com.pia.ticketmanagement.model.TicketSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketSubCategoryRepository extends JpaRepository<TicketSubCategory, Long> {

    boolean existsByNameAndCategory(String name, TicketCategory category);
    List<TicketSubCategory> findByCategoryId(Long categoryId);

    long countByCategoryId(Long categoryId);


}