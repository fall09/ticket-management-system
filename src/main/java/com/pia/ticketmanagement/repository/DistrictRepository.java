package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.District;
import com.pia.ticketmanagement.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRepository extends JpaRepository<District, Long> {
    boolean existsByNameAndProvince(String name, Province province);
}