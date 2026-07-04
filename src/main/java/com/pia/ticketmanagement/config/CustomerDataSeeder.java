package com.pia.ticketmanagement.config;

import com.pia.ticketmanagement.model.Customer;
import com.pia.ticketmanagement.model.CustomerStatus;
import com.pia.ticketmanagement.model.CustomerStatusHistory;
import com.pia.ticketmanagement.model.District;
import com.pia.ticketmanagement.model.InactiveReason;
import com.pia.ticketmanagement.model.Province;
import com.pia.ticketmanagement.model.SuspendedReason;
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
import java.time.LocalDateTime;
import java.util.Random;

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

        var inputStream = getClass().getResourceAsStream("/data/customers.csv");

        if (inputStream == null) {
            throw new RuntimeException("customers.csv not found");
        }

        Random random = new Random();

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

                String firstName = record.get(0).replace("\uFEFF", "").trim();
                String lastName = record.get(1).trim();
                String phoneNumber = record.get(2).trim();
                String email = record.get(3).trim();
                String provinceName = record.get(4).trim();
                String districtName = record.get(5).trim();

                if (customerRepository.existsByPhoneNumber(phoneNumber)
                        || customerRepository.existsByEmail(email)) {
                    continue;
                }

                Province province = provinceRepository.findByName(provinceName)
                        .orElseThrow(() ->
                                new RuntimeException("Province not found: " + provinceName));

                District district = districtRepository
                        .findByNameAndProvince(districtName, province)
                        .orElseThrow(() ->
                                new RuntimeException("District not found: "
                                        + districtName + " / " + provinceName));

                CustomerStatus status = randomCustomerStatus(random);

                Customer customer = Customer.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .phoneNumber(phoneNumber)
                        .email(email)
                        .province(province)
                        .district(district)
                        .status(status)
                        .build();

                Customer savedCustomer = customerRepository.save(customer);

                CustomerStatusHistory history = CustomerStatusHistory.builder()
                        .customer(savedCustomer)
                        .oldStatus(CustomerStatus.ACTIVE)
                        .newStatus(status)
                        .inactiveReason(status == CustomerStatus.INACTIVE ? randomInactiveReason(random) : null)
                        .suspendedReason(status == CustomerStatus.SUSPENDED ? randomSuspendedReason(random) : null)
                        .note(generateStatusNote(status))
                        .changedAt(LocalDateTime.now().minusDays(random.nextInt(180)))
                        .build();

                statusHistoryRepository.save(history);
            }
        }

        System.out.println("Customer data seeded successfully.");
    }

    private CustomerStatus randomCustomerStatus(Random random) {
        int value = random.nextInt(100);

        if (value < 88) {
            return CustomerStatus.ACTIVE;
        } else if (value < 95) {
            return CustomerStatus.SUSPENDED;
        } else {
            return CustomerStatus.INACTIVE;
        }
    }

    private InactiveReason randomInactiveReason(Random random) {
        InactiveReason[] reasons = {
                InactiveReason.CUSTOMER_REQUEST,
                InactiveReason.CONTRACT_TERMINATED,
                InactiveReason.CUSTOMER_DECEASED,
                InactiveReason.NUMBER_PORTED_OUT,
                InactiveReason.DUPLICATE_CUSTOMER,
                InactiveReason.OTHER
        };

        return reasons[random.nextInt(reasons.length)];
    }

    private SuspendedReason randomSuspendedReason(Random random) {
        SuspendedReason[] reasons = {
                SuspendedReason.PAYMENT_OVERDUE,
                SuspendedReason.FRAUD_SUSPICION,
                SuspendedReason.SECURITY_VERIFICATION,
                SuspendedReason.POLICY_VIOLATION,
                SuspendedReason.TEMPORARY_SERVICE_HOLD,
                SuspendedReason.OTHER
        };

        return reasons[random.nextInt(reasons.length)];
    }

    private String generateStatusNote(CustomerStatus status) {
        if (status == CustomerStatus.ACTIVE) {
            return "Initial active customer record created by seed data.";
        }

        if (status == CustomerStatus.INACTIVE) {
            return "Customer marked as inactive during seed data generation.";
        }

        return "Customer marked as suspended during seed data generation.";
    }
}