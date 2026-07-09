package com.pia.ticketmanagement.controller;

import com.pia.ticketmanagement.dto.request.CreateCustomerRequest;
import com.pia.ticketmanagement.dto.request.UpdateCustomerRequest;
import com.pia.ticketmanagement.dto.request.UpdateCustomerStatusRequest;
import com.pia.ticketmanagement.dto.response.CustomerResponse;
import com.pia.ticketmanagement.dto.response.CustomerStatusHistoryResponse;
import com.pia.ticketmanagement.dto.response.TicketResponse;
import com.pia.ticketmanagement.model.CustomerStatus;
import com.pia.ticketmanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Page<CustomerResponse> getAllCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) CustomerStatus status,
            @RequestParam(required = false) Long provinceId,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) Boolean hasTicket,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return customerService.getAllCustomers(
                search,
                status,
                provinceId,
                districtId,
                hasTicket,
                page,
                size
        );
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping("/{id}/tickets")
    public List<TicketResponse> getCustomerTickets(@PathVariable Long id) {
        return customerService.getCustomerTickets(id);
    }

    @PostMapping
    public CustomerResponse createCustomer(@RequestBody CreateCustomerRequest request) {
        return customerService.createCustomer(request);
    }

    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(
            @PathVariable Long id,
            @RequestBody UpdateCustomerRequest request
    ) {
        return customerService.updateCustomer(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    @PatchMapping("/{id}/status")
    public CustomerResponse updateCustomerStatus(
            @PathVariable Long id,
            @RequestBody UpdateCustomerStatusRequest request
    ) {
        return customerService.updateCustomerStatus(id, request);
    }

    @GetMapping("/{id}/status-history")
    public List<CustomerStatusHistoryResponse> getCustomerStatusHistory(@PathVariable Long id) {
        return customerService.getCustomerStatusHistory(id);
    }

    @GetMapping("/status-counts")
    public Map<String, Long> getCustomerStatusCounts() {
        return customerService.getCustomerStatusCounts();
    }
}