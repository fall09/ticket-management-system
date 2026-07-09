package com.pia.ticketmanagement.config;

import com.pia.ticketmanagement.model.*;
import com.pia.ticketmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class TicketDataSeeder implements CommandLineRunner {

    private final TicketRepository ticketRepository;
    private final TicketStatusHistoryRepository ticketStatusHistoryRepository;
    private final TicketActivityLogRepository ticketActivityLogRepository;

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final TicketCategoryRepository ticketCategoryRepository;
    private final TicketSubCategoryRepository ticketSubCategoryRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;

    private final Map<Long, Ticket> ticketMap = new HashMap<>();
    private final Map<Long, Employee> employeeMap = new HashMap<>();

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (ticketRepository.count() > 0) {
            System.out.println("Ticket seed skipped. Tickets already exist.");
            return;
        }

        cacheEmployees();

        seedTickets();
        seedTicketStatusHistory();
        seedTicketActivityLogs();

        System.out.println("Ticket data seeded successfully.");
    }

    private void cacheEmployees() {
        employeeRepository.findAll()
                .forEach(employee -> employeeMap.put(employee.getId(), employee));
    }

    private void seedTickets() throws Exception {
        List<Ticket> tickets = new ArrayList<>();

        List<Map<String, String>> rows = readCsv("data/generated_tickets.csv");

        for (Map<String, String> row : rows) {
            Long id = getLong(row, "id");

            Customer customer = customerRepository.findById(getLong(row, "customer_id"))
                    .orElseThrow(() -> new RuntimeException("Customer not found: " + row.get("customer_id")));

            TicketCategory category = ticketCategoryRepository.findById(getLong(row, "category_id"))
                    .orElseThrow(() -> new RuntimeException("Category not found: " + row.get("category_id")));

            TicketSubCategory subCategory = ticketSubCategoryRepository.findById(getLong(row, "sub_category_id"))
                    .orElseThrow(() -> new RuntimeException("Sub category not found: " + row.get("sub_category_id")));

            Province province = null;
            if (hasValue(row, "issue_province_id")) {
                province = provinceRepository.findById(getLong(row, "issue_province_id")).orElse(null);
            }

            District district = null;
            if (hasValue(row, "issue_district_id")) {
                district = districtRepository.findById(getLong(row, "issue_district_id")).orElse(null);
            }

            Employee assignedEmployee = null;
            if (hasValue(row, "assigned_employee_id")) {
                assignedEmployee = employeeMap.get(getLong(row, "assigned_employee_id"));
            }

            Ticket ticket = Ticket.builder()
                    .ticketNumber(row.get("ticket_number"))
                    .customer(customer)
                    .category(category)
                    .subCategory(subCategory)
                    .issueProvince(province)
                    .issueDistrict(district)
                    .assignedEmployee(assignedEmployee)
                    .status(TicketStatus.valueOf(row.get("status")))
                    .priority(TicketPriority.valueOf(row.get("priority")))
                    .description(row.getOrDefault("description", "Generated ticket."))
                    .resolutionNote(emptyToNull(row.get("resolution_note")))
                    .createdAt(getDateTime(row, "created_at"))
                    .updatedAt(getDateTime(row, "updated_at"))
                    .resolvedAt(getNullableDateTime(row, "resolved_at"))
                    .build();

            tickets.add(ticket);

            if (id != null) {
                ticketMap.put(id, ticket);
            }
        }

        List<Ticket> savedTickets = ticketRepository.saveAll(tickets);

        for (Ticket ticket : savedTickets) {
            ticketMap.put(ticket.getId(), ticket);
        }
    }

    private void seedTicketStatusHistory() throws Exception {
        List<TicketStatusHistory> histories = new ArrayList<>();

        List<Map<String, String>> rows = readCsv("data/generated_ticket_status_history.csv");

        for (Map<String, String> row : rows) {
            Ticket ticket = findTicket(getLong(row, "ticket_id"));

            TicketStatus oldStatus = hasValue(row, "old_status")
                    ? TicketStatus.valueOf(row.get("old_status"))
                    : null;

            TicketStatus newStatus = TicketStatus.valueOf(row.get("new_status"));

            TicketStatusHistory history = TicketStatusHistory.builder()
                    .ticket(ticket)
                    .oldStatus(oldStatus)
                    .newStatus(newStatus)
                    .note(emptyToNull(row.get("note")))
                    .changedAt(getDateTime(row, "changed_at"))
                    .build();

            histories.add(history);
        }

        ticketStatusHistoryRepository.saveAll(histories);
    }

    private void seedTicketActivityLogs() throws Exception {
        List<TicketActivityLog> logs = new ArrayList<>();

        List<Map<String, String>> rows = readCsv("data/generated_ticket_activity_logs.csv");

        for (Map<String, String> row : rows) {
            Ticket ticket = findTicket(getLong(row, "ticket_id"));

            Employee employee = null;
            if (hasValue(row, "employee_id")) {
                employee = employeeMap.get(getLong(row, "employee_id"));
            }

            TicketActivityLog log = TicketActivityLog.builder()
                    .ticket(ticket)
                    .employee(employee)
                    .type(TicketActivityType.valueOf(row.get("type")))
                    .oldValue(emptyToNull(row.get("old_value")))
                    .newValue(emptyToNull(row.get("new_value")))
                    .note(emptyToNull(row.get("note")))
                    .createdAt(getDateTime(row, "created_at"))
                    .build();

            logs.add(log);
        }

        ticketActivityLogRepository.saveAll(logs);
    }

    private Ticket findTicket(Long id) {
        Ticket ticket = ticketMap.get(id);

        if (ticket != null) {
            return ticket;
        }

        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found: " + id));
    }

    private List<Map<String, String>> readCsv(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);

        List<Map<String, String>> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        )) {
            String headerLine = reader.readLine();

            if (headerLine == null) {
                return rows;
            }

            String[] headers = parseCsvLine(headerLine);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = parseCsvLine(line);
                Map<String, String> row = new HashMap<>();

                for (int i = 0; i < headers.length; i++) {
                    String value = i < values.length ? values[i] : "";
                    row.put(headers[i].trim(), value.trim());
                }

                rows.add(row);
            }
        }

        return rows;
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);

            if (character == '"') {
                inQuotes = !inQuotes;
            } else if (character == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }

        result.add(current.toString());

        return result.toArray(new String[0]);
    }

    private Long getLong(Map<String, String> row, String key) {
        if (!hasValue(row, key)) {
            return null;
        }

        return Long.valueOf(row.get(key));
    }

    private LocalDateTime getDateTime(Map<String, String> row, String key) {
        return LocalDateTime.parse(row.get(key).replace(" ", "T"));
    }

    private LocalDateTime getNullableDateTime(Map<String, String> row, String key) {
        if (!hasValue(row, key)) {
            return null;
        }

        return getDateTime(row, key);
    }

    private boolean hasValue(Map<String, String> row, String key) {
        return row.containsKey(key)
                && row.get(key) != null
                && !row.get(key).isBlank()
                && !row.get(key).equalsIgnoreCase("null");
    }

    private String emptyToNull(String value) {
        if (value == null || value.isBlank() || value.equalsIgnoreCase("null")) {
            return null;
        }

        return value;
    }
}