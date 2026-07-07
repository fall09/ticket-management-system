package com.pia.ticketmanagement.controller;

import com.pia.ticketmanagement.dto.request.LoginEmployeeRequest;
import com.pia.ticketmanagement.dto.request.RegisterEmployeeRequest;
import com.pia.ticketmanagement.dto.response.AuthResponse;
import com.pia.ticketmanagement.service.EmployeeAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeAuthController {

    private final EmployeeAuthService employeeAuthService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterEmployeeRequest request) {
        return employeeAuthService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginEmployeeRequest request) {
        return employeeAuthService.login(request);
    }
}