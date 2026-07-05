package com.pia.ticketmanagement.config;

import com.pia.ticketmanagement.model.*;
import com.pia.ticketmanagement.repository.CustomerRepository;
import com.pia.ticketmanagement.repository.CustomerStatusHistoryRepository;
import com.pia.ticketmanagement.repository.TicketCategoryRepository;
import com.pia.ticketmanagement.repository.TicketRepository;
import com.pia.ticketmanagement.repository.TicketSubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class TicketDataSeeder implements CommandLineRunner {

    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final CustomerStatusHistoryRepository statusHistoryRepository;
    private final TicketCategoryRepository categoryRepository;
    private final TicketSubCategoryRepository subCategoryRepository;

    private final Random random = new Random();

    @Override
    public void run(String... args) {
        if (ticketRepository.count() > 0) return;

        List<Customer> customers = customerRepository.findAll();
        List<TicketCategory> categories = categoryRepository.findAll();

        if (customers.isEmpty() || categories.isEmpty()) {
            System.out.println("Ticket seeder skipped. Customers or categories not found.");
            return;
        }

        int ticketCount = 5000;

        for (int i = 1; i <= ticketCount; i++) {
            Customer customer = customers.get(random.nextInt(customers.size()));
            TicketCategory category = categories.get(random.nextInt(categories.size()));

            List<TicketSubCategory> subCategories =
                    subCategoryRepository.findByCategoryId(category.getId());

            if (subCategories.isEmpty()) continue;

            TicketSubCategory subCategory =
                    subCategories.get(random.nextInt(subCategories.size()));

            LocalDateTime maxTicketDate = getMaxTicketDateForCustomer(customer);
            LocalDateTime createdAt = randomDateBefore(maxTicketDate);

            TicketStatus status = randomStatus();
            LocalDateTime updatedAt = createdAt.plusDays(random.nextInt(10));

            if (updatedAt.isAfter(maxTicketDate)) {
                updatedAt = maxTicketDate.minusHours(1);
            }

            LocalDateTime resolvedAt =
                    status == TicketStatus.RESOLVED || status == TicketStatus.CLOSED
                            ? updatedAt.plusHours(random.nextInt(24))
                            : null;

            if (resolvedAt != null && resolvedAt.isAfter(maxTicketDate)) {
                resolvedAt = maxTicketDate.minusMinutes(30);
            }

            Ticket ticket = Ticket.builder()
                    .ticketNumber(String.format("TK-%06d", i))
                    .customer(customer)
                    .category(category)
                    .subCategory(subCategory)
                    .issueProvince(subCategory.isLocationRequired() ? customer.getProvince() : null)
                    .issueDistrict(subCategory.isLocationRequired() ? customer.getDistrict() : null)
                    .status(status)
                    .priority(subCategory.getDefaultPriority())
                    .description(generateDescription(category.getName(), subCategory.getName()))
                    .resolutionNote(generateResolutionNote(status))
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .resolvedAt(resolvedAt)
                    .build();

            ticketRepository.save(ticket);
        }

        System.out.println("Ticket data seeded successfully.");
    }

    private LocalDateTime getMaxTicketDateForCustomer(Customer customer) {
        if (customer.getStatus() == CustomerStatus.ACTIVE) {
            return LocalDateTime.now();
        }

        return statusHistoryRepository
                .findTopByCustomerIdOrderByChangedAtDesc(customer.getId())
                .map(CustomerStatusHistory::getChangedAt)
                .orElse(LocalDateTime.now());
    }

    private LocalDateTime randomDateBefore(LocalDateTime maxDate) {
        LocalDateTime date = maxDate
                .minusDays(random.nextInt(180) + 1)
                .minusHours(random.nextInt(24))
                .minusMinutes(random.nextInt(60));

        return date;
    }

    private TicketStatus randomStatus() {
        int value = random.nextInt(100);

        if (value < 45) return TicketStatus.OPEN;
        if (value < 75) return TicketStatus.IN_PROGRESS;
        if (value < 92) return TicketStatus.RESOLVED;
        return TicketStatus.CLOSED;
    }

    private String generateDescription(String category, String subCategory) {
        String lower = category.toLowerCase();

        if (lower.contains("internet")) {
            return "Customer reports an internet issue related to " + subCategory +
                    ". Connection quality is affecting daily usage.";
        }

        if (lower.contains("mobile")) {
            return "Customer reports a mobile service problem related to " + subCategory +
                    ". Issue needs technical review.";
        }

        if (lower.contains("billing")) {
            return "Customer reports a billing issue related to " + subCategory +
                    ". Invoice details should be checked.";
        }

        if (lower.contains("tv")) {
            return "Customer reports a TV service issue related to " + subCategory +
                    ". Service quality is affected.";
        }

        if (lower.contains("complaint")) {
            return "Customer submitted a complaint about " + subCategory +
                    ". Case requires follow-up.";
        }

        return "Customer reported an issue related to " + subCategory + ".";
    }

    private String generateResolutionNote(TicketStatus status) {
        if (status == TicketStatus.OPEN || status == TicketStatus.IN_PROGRESS) {
            return null;
        }

        String[] notes = {
                "Issue resolved after technical review.",
                "Customer was contacted and the problem was fixed.",
                "Configuration updated successfully.",
                "Service restored after investigation.",
                "Case closed after customer confirmation."
        };

        return notes[random.nextInt(notes.length)];
    }
}