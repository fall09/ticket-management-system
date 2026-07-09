package com.pia.ticketmanagement.service;

import com.pia.ticketmanagement.dto.request.CreateTicketRequest;
import com.pia.ticketmanagement.dto.request.UpdateTicketRequest;
import com.pia.ticketmanagement.dto.response.*;
import com.pia.ticketmanagement.exception.BadRequestException;
import com.pia.ticketmanagement.exception.NotFoundException;
import com.pia.ticketmanagement.model.*;
import com.pia.ticketmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final TicketCategoryRepository categoryRepository;
    private final TicketSubCategoryRepository subCategoryRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final TicketStatusHistoryRepository ticketStatusHistoryRepository;
    private final TicketActivityLogRepository ticketActivityLogRepository;

    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TicketResponse getTicketById(Long id) {
        return mapToResponse(findTicketById(id));
    }

    public TicketResponse createTicket(CreateTicketRequest request, Employee employee) {
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
                .assignedEmployee(Boolean.TRUE.equals(request.getAssignToMe()) ? employee : null)
                .status(TicketStatus.OPEN)
                .priority(request.getPriority() != null ? request.getPriority() : subCategory.getDefaultPriority())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        createActivityLog(
                savedTicket,
                employee,
                TicketActivityType.CREATED,
                null,
                savedTicket.getTicketNumber(),
                "Ticket created."
        );

        if (savedTicket.getAssignedEmployee() != null) {
            createActivityLog(
                    savedTicket,
                    employee,
                    TicketActivityType.ASSIGNED,
                    null,
                    employee.getFirstName() + " " + employee.getLastName(),
                    "Ticket assigned during creation."
            );
        }

        return mapToResponse(savedTicket);
    }

    public List<TicketResponse> getTicketPool() {
        return ticketRepository.findByAssignedEmployeeIsNull()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<TicketResponse> getMyTickets(Employee employee) {
        return ticketRepository.findByAssignedEmployeeId(employee.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TicketResponse takeTicket(Long ticketId, Employee employee) {
        Ticket ticket = findTicketById(ticketId);

        if (ticket.getAssignedEmployee() != null) {
            throw new BadRequestException("This ticket is already assigned.");
        }

        ticket.setAssignedEmployee(employee);
        ticket.setUpdatedAt(LocalDateTime.now());

        Ticket savedTicket = ticketRepository.save(ticket);

        createActivityLog(
                savedTicket,
                employee,
                TicketActivityType.ASSIGNED,
                null,
                employee.getFirstName() + " " + employee.getLastName(),
                "Ticket taken by employee."
        );

        return mapToResponse(savedTicket);
    }

    public List<TicketStatusHistoryResponse> getTicketStatusHistory(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new NotFoundException("Ticket not found.");
        }

        return ticketStatusHistoryRepository.findByTicketIdOrderByChangedAtDesc(ticketId)
                .stream()
                .map(item -> TicketStatusHistoryResponse.builder()
                        .id(item.getId())
                        .oldStatus(item.getOldStatus())
                        .newStatus(item.getNewStatus())
                        .note(item.getNote())
                        .changedAt(item.getChangedAt())
                        .build())
                .toList();
    }

    public TicketResponse updateTicket(Long id, UpdateTicketRequest request, Employee employee) {
        Ticket ticket = findTicketById(id);

        TicketStatus oldStatus = ticket.getStatus();
        TicketPriority oldPriority = ticket.getPriority();
        String oldDescription = ticket.getDescription();

        ticket.setStatus(request.getStatus());
        ticket.setPriority(request.getPriority());
        ticket.setDescription(request.getDescription());
        ticket.setUpdatedAt(LocalDateTime.now());

        if (request.getStatus() == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }

        if (request.getStatus() != TicketStatus.RESOLVED) {
            ticket.setResolvedAt(null);
        }

        Ticket savedTicket = ticketRepository.save(ticket);

        if (oldStatus != request.getStatus()) {
            TicketStatusHistory history = TicketStatusHistory.builder()
                    .ticket(savedTicket)
                    .oldStatus(oldStatus)
                    .newStatus(request.getStatus())
                    .note(request.getNote())
                    .changedAt(LocalDateTime.now())
                    .build();

            ticketStatusHistoryRepository.save(history);

            createActivityLog(
                    savedTicket,
                    employee,
                    TicketActivityType.STATUS_CHANGED,
                    oldStatus.name(),
                    request.getStatus().name(),
                    request.getNote()
            );
        }

        if (oldPriority != request.getPriority()) {
            createActivityLog(
                    savedTicket,
                    employee,
                    TicketActivityType.PRIORITY_CHANGED,
                    oldPriority.name(),
                    request.getPriority().name(),
                    "Priority updated."
            );
        }

        if (oldDescription != null && request.getDescription() != null && !oldDescription.equals(request.getDescription())) {
            createActivityLog(
                    savedTicket,
                    employee,
                    TicketActivityType.DESCRIPTION_UPDATED,
                    null,
                    null,
                    "Description updated."
            );
        }

        return mapToResponse(savedTicket);
    }

    private Ticket findTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found."));
    }

    public TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .customerId(ticket.getCustomer().getId())
                .customerName(ticket.getCustomer().getFirstName() + " " + ticket.getCustomer().getLastName())
                .customerPhone(ticket.getCustomer().getPhoneNumber())
                .category(ticket.getCategory().getName())
                .subCategory(ticket.getSubCategory().getName())
                .assignedEmployeeId(ticket.getAssignedEmployee() != null ? ticket.getAssignedEmployee().getId() : null)
                .assignedEmployeeName(ticket.getAssignedEmployee() != null
                        ? ticket.getAssignedEmployee().getFirstName() + " " + ticket.getAssignedEmployee().getLastName()
                        : null)
                .issueProvince(ticket.getIssueProvince() != null ? ticket.getIssueProvince().getName() : null)
                .issueDistrict(ticket.getIssueDistrict() != null ? ticket.getIssueDistrict().getName() : null)
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
        Long maxNumber = ticketRepository.findMaxTicketNumber();
        long nextNumber = maxNumber == null ? 1 : maxNumber + 1;
        return String.format("TK-%06d", nextNumber);
    }

    private void createActivityLog(
            Ticket ticket,
            Employee employee,
            TicketActivityType type,
            String oldValue,
            String newValue,
            String note
    ) {
        TicketActivityLog log = TicketActivityLog.builder()
                .ticket(ticket)
                .employee(employee)
                .type(type)
                .oldValue(oldValue)
                .newValue(newValue)
                .note(note)
                .createdAt(LocalDateTime.now())
                .build();

        ticketActivityLogRepository.save(log);
    }

    public List<TicketActivityLogResponse> getTicketActivities(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new NotFoundException("Ticket not found.");
        }

        return ticketActivityLogRepository.findByTicketIdOrderByCreatedAtDesc(ticketId)
                .stream()
                .map(log -> TicketActivityLogResponse.builder()
                        .id(log.getId())
                        .type(log.getType())
                        .oldValue(log.getOldValue())
                        .newValue(log.getNewValue())
                        .note(log.getNote())
                        .employeeId(log.getEmployee() != null ? log.getEmployee().getId() : null)
                        .employeeName(log.getEmployee() != null
                                ? log.getEmployee().getFirstName() + " " + log.getEmployee().getLastName()
                                : null)
                        .createdAt(log.getCreatedAt())
                        .build())
                .toList();
    }

    public TicketSummaryResponse getTicketSummary() {
        long totalTickets = ticketRepository.count();

        long openTickets = ticketRepository.findByStatus(TicketStatus.OPEN).size();
        long inProgressTickets = ticketRepository.findByStatus(TicketStatus.IN_PROGRESS).size();
        long onHoldTickets = ticketRepository.findByStatus(TicketStatus.ON_HOLD).size();
        long resolvedOnlyTickets = ticketRepository.findByStatus(TicketStatus.RESOLVED).size();
        long closedTickets = ticketRepository.findByStatus(TicketStatus.CLOSED).size();
        long resolvedTickets = resolvedOnlyTickets + closedTickets;

        long criticalTickets = ticketRepository.findByPriority(TicketPriority.CRITICAL).size();

        int resolutionRate = totalTickets == 0
                ? 0
                : (int) ((resolvedTickets * 100) / totalTickets);

        return TicketSummaryResponse.builder()
                .totalTickets(totalTickets)
                .openTickets(openTickets)
                .inProgressTickets(inProgressTickets)
                .onHoldTickets(onHoldTickets)
                .resolvedTickets(resolvedTickets)
                .closedTickets(closedTickets)
                .criticalTickets(criticalTickets)
                .resolutionRate(resolutionRate)
                .build();
    }

    public TopItemResponse getTopCategory() {

        List<Object[]> result = ticketRepository.getCategoryDistribution();

        result.forEach(row ->
                System.out.println(row[0] + " -> " + row[1]));

        if (result.isEmpty()) {
            return TopItemResponse.builder()
                    .name("None")
                    .value(0L)
                    .build();
        }

        Object[] row = result.get(0);

        return TopItemResponse.builder()
                .name(String.valueOf(row[0]))
                .value(((Number) row[1]).longValue())
                .build();
    }

    public TopItemResponse getTopProvince() {

        List<Object[]> result = ticketRepository.getProvinceDistribution();

        if (result.isEmpty()) {
            return TopItemResponse.builder()
                    .name("None")
                    .value(0L)
                    .build();
        }

        Object[] row = result.get(0);

        return TopItemResponse.builder()
                .name(String.valueOf(row[0]))
                .value(((Number) row[1]).longValue())
                .build();
    }

    public TopItemResponse getTopPriority() {

        List<Object[]> result = ticketRepository.getPriorityDistribution();

        if (result.isEmpty()) {
            return TopItemResponse.builder()
                    .name("None")
                    .value(0L)
                    .build();
        }

        Object[] row = result.get(0);

        return TopItemResponse.builder()
                .name(String.valueOf(row[0]))
                .value(((Number) row[1]).longValue())
                .build();
    }

    public TopItemResponse getTopSubCategory() {

        List<Object[]> result = ticketRepository.getSubCategoryDistribution();

        if (result.isEmpty()) {

            return TopItemResponse.builder()

                    .name("None")

                    .value(0L)

                    .build();

        }

        Object[] row = result.get(0);

        return TopItemResponse.builder()

                .name(String.valueOf(row[0]))

                .value(((Number) row[1]).longValue())

                .build();

    }
    public List<Object[]> getCategoryDistribution() {

        return ticketRepository.getCategoryDistribution();

    }

    public List<Object[]> getPriorityDistribution() {

        return ticketRepository.getPriorityDistribution();

    }
    public List<Object[]> getProvinceDistribution() {
        return ticketRepository.getProvinceDistribution();
    }

    public List<Map<String, Object>> getDailyTrend() {
        return ticketRepository.getDailyTrend()
                .stream()
                .map(row -> {
                    Map<String, Object> item = new java.util.HashMap<>();
                    item.put("date", String.valueOf(row[0]));
                    item.put("Created", ((Number) row[1]).longValue());
                    item.put("Resolved", row[2] == null ? 0L : ((Number) row[2]).longValue());
                    return item;
                })
                .toList();
    }


}