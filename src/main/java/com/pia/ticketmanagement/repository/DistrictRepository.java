package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.District;
import com.pia.ticketmanagement.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Long> {
    boolean existsByNameAndProvince(String name, Province province);
    Optional<District> findByNameAndProvince(String name, Province province);
    List<District> findByProvince(Province province);
}