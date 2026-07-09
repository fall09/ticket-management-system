package com.pia.ticketmanagement.config;

import com.pia.ticketmanagement.model.Employee;
import com.pia.ticketmanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EmployeeDataSeeder implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        if (employeeRepository.count() > 0) {
            return;
        }

        var inputStream = getClass().getResourceAsStream("/data/employees.csv");

        if (inputStream == null) {
            throw new RuntimeException("employees.csv not found.");
        }

        int count = 0;

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

                String email = record.get("email").trim();

                if (employeeRepository.existsByEmail(email)) {
                    continue;
                }

                Employee employee = Employee.builder()
                        .createdAt(LocalDateTime.parse(
                                record.get("created_at")
                                        .trim()
                                        .replace(" ", "T")
                        ))
                        .firstName(record.get("first_name").trim())
                        .lastName(record.get("last_name").trim())
                        .email(email)
                        .password(passwordEncoder.encode(record.get("password").trim()))
                        .build();

                employeeRepository.save(employee);
                count++;
            }
        }

        System.out.println("Employee CSV seed completed. Imported: " + count);
    }
}