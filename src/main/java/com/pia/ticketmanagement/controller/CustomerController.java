package com.pia.ticketmanagement.controller;

import com.pia.ticketmanagement.dto.request.CreateCustomerRequest;
import com.pia.ticketmanagement.dto.request.UpdateCustomerRequest;
import com.pia.ticketmanagement.dto.request.UpdateCustomerStatusRequest;
import com.pia.ticketmanagement.dto.response.CustomerResponse;
import com.pia.ticketmanagement.dto.response.CustomerStatusHistoryResponse;
import com.pia.ticketmanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public List<CustomerResponse> getAllCustomers(@RequestParam(required = false) String search) {
        return customerService.getAllCustomers(search);
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
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

}