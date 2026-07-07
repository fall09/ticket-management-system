package com.pia.ticketmanagement.service;

import com.pia.ticketmanagement.dto.response.TicketResponse;
import com.pia.ticketmanagement.model.Ticket;
import com.pia.ticketmanagement.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.pia.ticketmanagement.dto.request.CreateTicketRequest;
import com.pia.ticketmanagement.exception.BadRequestException;
import com.pia.ticketmanagement.exception.NotFoundException;
import com.pia.ticketmanagement.model.*;
import com.pia.ticketmanagement.repository.*;

import java.time.LocalDateTime;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final TicketCategoryRepository categoryRepository;
    private final TicketSubCategoryRepository subCategoryRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;


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

    TicketResponse mapToResponse(Ticket ticket) {
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
    private String generateTicketNumber() {
        long nextNumber = ticketRepository.count() + 1;
        return String.format("TK-%06d", nextNumber);
    }

    public TicketResponse createTicket(CreateTicketRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found."));

        TicketCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found."));

        TicketSubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                .orElseThrow(() -> new NotFoundException("Sub category not found."));

        if (!subCategory.getCategory().getId().equals(category.getId())) {
            throw new BadRequestException("Selected sub category does not belong to selected category.");
        }

        Province issueProvince = customer.getProvince();
        District issueDistrict = customer.getDistrict();

        if (subCategory.isLocationRequired()) {
            if (request.getProvinceId() == null || request.getDistrictId() == null) {
                throw new BadRequestException("Province and district are required for this sub category.");
            }

            issueProvince = provinceRepository.findById(request.getProvinceId())
                    .orElseThrow(() -> new NotFoundException("Province not found."));

            issueDistrict = districtRepository.findById(request.getDistrictId())
                    .orElseThrow(() -> new NotFoundException("District not found."));

            if (!issueDistrict.getProvince().getId().equals(issueProvince.getId())) {
                throw new BadRequestException("Selected district does not belong to selected province.");
            }
        }

        Ticket ticket = Ticket.builder()
                .ticketNumber(generateTicketNumber())
                .customer(customer)
                .category(category)
                .subCategory(subCategory)
                .issueProvince(issueProvince)
                .issueDistrict(issueDistrict)
                .status(TicketStatus.OPEN)
                .priority(subCategory.getDefaultPriority())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return mapToResponse(ticketRepository.save(ticket));
    }
}