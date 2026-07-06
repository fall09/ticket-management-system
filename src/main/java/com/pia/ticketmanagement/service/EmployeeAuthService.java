package com.pia.ticketmanagement.service;

import com.pia.ticketmanagement.dto.request.LoginEmployeeRequest;
import com.pia.ticketmanagement.dto.request.RegisterEmployeeRequest;
import com.pia.ticketmanagement.dto.response.EmployeeResponse;
import com.pia.ticketmanagement.exception.BadRequestException;
import com.pia.ticketmanagement.exception.ConflictException;
import com.pia.ticketmanagement.exception.NotFoundException;
import com.pia.ticketmanagement.model.Employee;
import com.pia.ticketmanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmployeeAuthService {

    private final EmployeeRepository employeeRepository;

    public EmployeeResponse register(RegisterEmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered.");
        }

        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .createdAt(LocalDateTime.now())
                .build();

        return mapToResponse(employeeRepository.save(employee));
    }

    public EmployeeResponse login(LoginEmployeeRequest request) {
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Employee not found."));


        if (!employee.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Invalid email or password.");
        }

        return mapToResponse(employee);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .build();
    }
}