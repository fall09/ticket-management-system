package com.pia.ticketmanagement.controller;

import com.pia.ticketmanagement.dto.request.SystemSettingsRequest;
import com.pia.ticketmanagement.dto.response.SystemSettingsResponse;
import com.pia.ticketmanagement.service.SystemSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SystemSettingsController {

    private final SystemSettingsService service;

    @GetMapping
    public SystemSettingsResponse getSettings() {
        return service.getSettings();
    }

    @PutMapping
    public SystemSettingsResponse updateSettings(
            @RequestBody SystemSettingsRequest request
    ) {
        return service.updateSettings(request);
    }
}