package com.pia.ticketmanagement.config;

import com.pia.ticketmanagement.model.TicketPriority;
import com.pia.ticketmanagement.model.TicketCategory;
import com.pia.ticketmanagement.model.TicketSubCategory;
import com.pia.ticketmanagement.repository.TicketCategoryRepository;
import com.pia.ticketmanagement.repository.TicketSubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class TicketCategoryDataSeeder implements CommandLineRunner {

    private final TicketCategoryRepository categoryRepository;
    private final TicketSubCategoryRepository subCategoryRepository;

    @Override
    public void run(String... args) throws Exception {

        var inputStream = getClass().getResourceAsStream("/data/ticket_categories.csv");

        if (inputStream == null) {
            throw new RuntimeException("ticket_categories.csv not found");
        }

        try (
                var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                var csvParser = new CSVParser(
                        reader,
                        CSVFormat.DEFAULT.builder()
                                .setHeader()
                                .setSkipHeaderRecord(true)
                                .build()
                )
        ) {
            for (var record : csvParser) {

                String categoryName = record.get(0).replace("\uFEFF", "").trim();
                String subCategoryName = record.get(1).trim();
                boolean locationRequired = Boolean.parseBoolean(record.get(2).trim());
                TicketPriority defaultPriority = TicketPriority.valueOf(record.get(3).trim());

                String availableForNewCustomerText = record.isMapped("available_for_new_customer")
                        ? record.get("available_for_new_customer").trim()
                        : record.size() > 4 ? record.get(4).trim() : "false";

                final boolean availableForNewCustomer = Boolean.parseBoolean(availableForNewCustomerText);

                TicketCategory category = categoryRepository.findByName(categoryName)
                        .orElseGet(() -> categoryRepository.save(
                                TicketCategory.builder()
                                        .name(categoryName)
                                        .build()
                        ));

                subCategoryRepository.findByNameAndCategory(subCategoryName, category)
                        .ifPresentOrElse(existing -> {
                            existing.setLocationRequired(locationRequired);
                            existing.setDefaultPriority(defaultPriority);
                            existing.setAvailableForNewCustomer(availableForNewCustomer);
                            subCategoryRepository.save(existing);
                        }, () -> {
                            TicketSubCategory subCategory = TicketSubCategory.builder()
                                    .name(subCategoryName)
                                    .locationRequired(locationRequired)
                                    .defaultPriority(defaultPriority)
                                    .availableForNewCustomer(availableForNewCustomer)
                                    .category(category)
                                    .build();

                            subCategoryRepository.save(subCategory);
                        });
            }
        }

        System.out.println("Ticket categories synchronized successfully.");
    }
}