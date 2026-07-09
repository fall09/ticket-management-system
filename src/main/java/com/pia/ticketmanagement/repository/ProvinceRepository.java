package com.pia.ticketmanagement.repository;

import com.pia.ticketmanagement.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<Province, Long> {

    Optional<Province> findByName(String name);
    Optional<Province> findByNameIgnoreCase(String name);

}