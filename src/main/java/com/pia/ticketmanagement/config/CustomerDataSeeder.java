package com.pia.ticketmanagement.config;

import com.pia.ticketmanagement.model.CustomerStatus;
import com.pia.ticketmanagement.model.Customer;
import com.pia.ticketmanagement.model.District;
import com.pia.ticketmanagement.model.Province;
import com.pia.ticketmanagement.repository.CustomerRepository;
import com.pia.ticketmanagement.repository.DistrictRepository;
import com.pia.ticketmanagement.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CustomerDataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;

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

                CustomerStatus status;

                int value = random.nextInt(100);

                if (value < 88) {
                    status = CustomerStatus.ACTIVE;
                } else if (value < 95) {
                    status = CustomerStatus.SUSPENDED;
                } else {
                    status = CustomerStatus.INACTIVE;
                }

                Customer customer = Customer.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .phoneNumber(phoneNumber)
                        .email(email)
                        .province(province)
                        .district(district)
                        .status(status)
                        .build();

                customerRepository.save(customer);
            }
        }

        System.out.println("Customer data seeded successfully.");
    }
}