package com.pia.ticketmanagement.config;

import com.pia.ticketmanagement.model.District;
import com.pia.ticketmanagement.model.Province;
import com.pia.ticketmanagement.repository.DistrictRepository;
import com.pia.ticketmanagement.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class LocationDataSeeder implements CommandLineRunner {

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;

    @Override
    public void run(String... args) throws Exception {

        if (provinceRepository.count() > 0) {
            return;
        }

        var inputStream = getClass().getResourceAsStream("/data/il_ilce.csv");

        if (inputStream == null) {
            throw new RuntimeException("il_ilce.csv not found");
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
                String provinceName = record.get("il").trim();
                String districtName = record.get("ilce").trim();

                Province province = provinceRepository.findByName(provinceName)
                        .orElseGet(() -> provinceRepository.save(
                                Province.builder()
                                        .name(provinceName)
                                        .build()
                        ));

                boolean districtExists =
                        districtRepository.existsByNameAndProvince(districtName, province);

                if (!districtExists) {
                    District district = District.builder()
                            .name(districtName)
                            .province(province)
                            .build();

                    districtRepository.save(district);
                }
            }
        }
    }
}