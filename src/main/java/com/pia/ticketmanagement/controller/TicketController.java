package com.pia.ticketmanagement.controller;

import com.pia.ticketmanagement.dto.request.CreateTicketRequest;
import com.pia.ticketmanagement.dto.response.TicketResponse;
import com.pia.ticketmanagement.dto.response.TicketStatusHistoryResponse;
import com.pia.ticketmanagement.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.pia.ticketmanagement.model.Employee;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    public List<TicketResponse> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/{id}")
    public TicketResponse getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @PostMapping
    public TicketResponse createTicket(@RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(request);
    }

    @GetMapping("/pool")
    public List<TicketResponse> getTicketPool() {
        return ticketService.getTicketPool();
    }

    @GetMapping("/my")
    public List<TicketResponse> getMyTickets(Authentication authentication) {
        Employee employee = (Employee) authentication.getPrincipal();
        return ticketService.getMyTickets(employee);
    }

    @PatchMapping("/{id}/take")
    public TicketResponse takeTicket(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Employee employee = (Employee) authentication.getPrincipal();
        return ticketService.takeTicket(id, employee);
    }

    @GetMapping("/{id}/status-history")
    public List<TicketStatusHistoryResponse> getTicketStatusHistory(@PathVariable Long id) {
        return ticketService.getTicketStatusHistory(id);
    }
}