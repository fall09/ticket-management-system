package com.pia.ticketmanagement.service;

import com.pia.ticketmanagement.dto.request.LoginEmployeeRequest;
import com.pia.ticketmanagement.dto.request.RegisterEmployeeRequest;
import com.pia.ticketmanagement.dto.response.AuthResponse;
import com.pia.ticketmanagement.dto.response.EmployeeResponse;
import com.pia.ticketmanagement.exception.BadRequestException;
import com.pia.ticketmanagement.exception.ConflictException;
import com.pia.ticketmanagement.exception.NotFoundException;
import com.pia.ticketmanagement.model.Employee;
import com.pia.ticketmanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmployeeAuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterEmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered.");
        }

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();

        Employee saved = employeeRepository.save(employee);

        return AuthResponse.builder()
                .token(jwtService.generateToken(saved))
                .employee(mapToResponse(saved))
                .build();
    }

    public AuthResponse login(LoginEmployeeRequest request) {
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Employee not found."));

        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new BadRequestException("Invalid email or password.");
        }

        return AuthResponse.builder()
                .token(jwtService.generateToken(employee))
                .employee(mapToResponse(employee))
                .build();
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .createdAt(employee.getCreatedAt())
                .build();
    }
}