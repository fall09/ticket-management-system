package com.pia.ticketmanagement.service;

import com.pia.ticketmanagement.dto.response.TicketResponse;
import com.pia.ticketmanagement.model.Ticket;
import com.pia.ticketmanagement.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TicketResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        return mapToResponse(ticket);
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .customerId(ticket.getCustomer().getId())
                .customerName(
                        ticket.getCustomer().getFirstName() + " " +
                                ticket.getCustomer().getLastName()
                )
                .customerPhone(ticket.getCustomer().getPhoneNumber())
                .category(ticket.getCategory().getName())
                .subCategory(ticket.getSubCategory().getName())
                .issueProvince(
                        ticket.getIssueProvince() != null
                                ? ticket.getIssueProvince().getName()
                                : null
                )
                .issueDistrict(
                        ticket.getIssueDistrict() != null
                                ? ticket.getIssueDistrict().getName()
                                : null
                )
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .description(ticket.getDescription())
                .resolutionNote(ticket.getResolutionNote())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .build();
    }
}