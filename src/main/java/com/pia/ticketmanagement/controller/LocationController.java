package com.pia.ticketmanagement.controller;

import com.pia.ticketmanagement.dto.response.LocationResponse;
import com.pia.ticketmanagement.model.Province;
import com.pia.ticketmanagement.repository.DistrictRepository;
import com.pia.ticketmanagement.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class LocationController {

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;

    @GetMapping("/provinces")
    public List<LocationResponse> getProvinces() {
        return provinceRepository.findAll()
                .stream()
                .map(province -> LocationResponse.builder()
                        .id(province.getId())
                        .name(province.getName())
                        .build())
                .toList();
    }

    @GetMapping("/districts")
    public List<LocationResponse> getDistrictsByProvince(@RequestParam Long provinceId) {
        Province province = provinceRepository.findById(provinceId)
                .orElseThrow(() -> new RuntimeException("Province not found"));

        return districtRepository.findByProvince(province)
                .stream()
                .map(district -> LocationResponse.builder()
                        .id(district.getId())
                        .name(district.getName())
                        .build())
                .toList();
    }
}