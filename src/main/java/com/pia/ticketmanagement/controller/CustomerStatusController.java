package com.pia.ticketmanagement.controller;

import com.pia.ticketmanagement.dto.response.EnumOptionResponse;
import com.pia.ticketmanagement.model.CustomerStatus;
import com.pia.ticketmanagement.model.InactiveReason;
import com.pia.ticketmanagement.model.SuspendedReason;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/customer-statuses")
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerStatusController {

    @GetMapping
    public List<EnumOptionResponse> getStatuses() {
        return Arrays.stream(CustomerStatus.values())
                .map(status -> new EnumOptionResponse(
                        status.name(),
                        format(status.name())
                ))
                .toList();
    }

    @GetMapping("/inactive-reasons")
    public List<EnumOptionResponse> getInactiveReasons() {
        return Arrays.stream(InactiveReason.values())
                .map(reason -> new EnumOptionResponse(
                        reason.name(),
                        format(reason.name())
                ))
                .toList();
    }

    @GetMapping("/suspended-reasons")
    public List<EnumOptionResponse> getSuspendedReasons() {
        return Arrays.stream(SuspendedReason.values())
                .map(reason -> new EnumOptionResponse(
                        reason.name(),
                        format(reason.name())
                ))
                .toList();
    }

    private String format(String value) {
        String lower = value.toLowerCase().replace("_", " ");
        return lower.substring(0, 1).toUpperCase() + lower.substring(1);
    }
}