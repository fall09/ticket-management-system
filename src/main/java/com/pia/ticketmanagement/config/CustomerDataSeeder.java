package com.pia.ticketmanagement.config;

import com.pia.ticketmanagement.model.*;
import com.pia.ticketmanagement.repository.CustomerRepository;
import com.pia.ticketmanagement.repository.CustomerStatusHistoryRepository;
import com.pia.ticketmanagement.repository.DistrictRepository;
import com.pia.ticketmanagement.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class CustomerDataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final CustomerStatusHistoryRepository statusHistoryRepository;

    @Override
    public void run(String... args) throws Exception {

        if (customerRepository.count() > 0) {
            return;
        }

        var inputStream = getClass()
                .getResourceAsStream("/data/final_customers_list.csv");

        if (inputStream == null) {
            throw new RuntimeException("Customer CSV file not found.");
        }

        int count = 0;
        int skipped = 0;

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
                try {
                    String firstName = record.get(0).replace("\uFEFF", "").trim();
                    String lastName = record.get(1).trim();
                    String email = record.get(2).trim();
                    String phoneNumber = record.get(3).trim();
                    String provinceName = record.get(4).trim();
                    String districtName = record.get(5).trim();
                    String statusText = record.get(6).trim();
                    String inactiveReasonText = record.get(7).trim();
                    String suspendedReasonText = record.get(8).trim();

                    CustomerStatus status = CustomerStatus.valueOf(statusText);

                    if (customerRepository.existsByPhoneNumber(phoneNumber)
                            || customerRepository.existsByEmail(email)) {
                        skipped++;
                        continue;
                    }

                    Province province = provinceRepository.findAll()
                            .stream()
                            .filter(p -> sameText(p.getName(), provinceName))
                            .findFirst()
                            .orElseThrow(() ->
                                    new RuntimeException("Province not found: " + provinceName));

                    District district = districtRepository.findAll()
                            .stream()
                            .filter(d ->
                                    d.getProvince().getId().equals(province.getId())
                                            && sameText(d.getName(), districtName)
                            )
                            .findFirst()
                            .orElseThrow(() ->
                                    new RuntimeException("District not found: "
                                            + districtName + " / " + provinceName));

                    Customer customer = Customer.builder()
                            .firstName(firstName)
                            .lastName(lastName)
                            .email(email)
                            .phoneNumber(phoneNumber)
                            .province(province)
                            .district(district)
                            .status(status)
                            .build();

                    Customer savedCustomer = customerRepository.save(customer);

                    if (status != CustomerStatus.ACTIVE) {
                        CustomerStatusHistory history = CustomerStatusHistory.builder()
                                .customer(savedCustomer)
                                .oldStatus(CustomerStatus.ACTIVE)
                                .newStatus(status)
                                .inactiveReason(
                                        status == CustomerStatus.INACTIVE && !inactiveReasonText.isBlank()
                                                ? InactiveReason.valueOf(inactiveReasonText)
                                                : null
                                )
                                .suspendedReason(
                                        status == CustomerStatus.SUSPENDED && !suspendedReasonText.isBlank()
                                                ? SuspendedReason.valueOf(suspendedReasonText)
                                                : null
                                )
                                .note("Status was assigned during seed data generation.")
                                .changedAt(LocalDateTime.now())
                                .build();

                        statusHistoryRepository.save(history);
                    }
                    count++;

                } catch (Exception e) {
                    skipped++;
                    System.out.println("Skipped row: " + record);
                    System.out.println("Reason: " + e.getMessage());
                }
            }
        }

        System.out.println("Customer CSV seed completed. Imported: " + count);
        System.out.println("Skipped rows: " + skipped);
    }

    private boolean sameText(String dbValue, String csvValue) {
        return normalize(dbValue).equals(normalize(csvValue));
    }

    private String normalize(String value) {
        if (value == null) return "";

        String normalized = value
                .trim()
                .replace("İ", "I")
                .replace("ı", "i")
                .toUpperCase(Locale.ROOT);

        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return normalized;
    }
}