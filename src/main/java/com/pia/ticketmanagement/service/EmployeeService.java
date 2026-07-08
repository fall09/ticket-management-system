package com.pia.ticketmanagement.service;

import com.pia.ticketmanagement.dto.response.EmployeeResponse;
import com.pia.ticketmanagement.exception.BadRequestException;
import com.pia.ticketmanagement.exception.NotFoundException;
import com.pia.ticketmanagement.model.Employee;
import com.pia.ticketmanagement.repository.EmployeeRepository;
import com.pia.ticketmanagement.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TicketRepository ticketRepository;

    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EmployeeResponse getEmployeeById(Long id) {
        return mapToResponse(findEmployeeById(id));
    }

    public void deleteEmployee(Long id) {
        Employee employee = findEmployeeById(id);

        long assignedTicketCount = ticketRepository.countByAssignedEmployeeId(id);

        if (assignedTicketCount > 0) {
            throw new BadRequestException(
                    "This employee has assigned tickets. Reassign or close tickets before deleting."
            );
        }

        employeeRepository.delete(employee);
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found."));
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