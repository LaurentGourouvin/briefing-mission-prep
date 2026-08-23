package com.briefing.aeronef.config;

import com.briefing.aeronef.domain.Aeronef;
import com.briefing.aeronef.repo.AeronefRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedAeronefs(AeronefRepository repository) {

        return args -> {
            if (repository.count() == 0) {
                var aeronefs = List.of(
                        new Aeronef("RAFALE_B"),
                        new Aeronef("RAFALE_C"),
                        new Aeronef("RAFALE_M"),
                        new Aeronef("FALCON_2000"),
                        new Aeronef("FALCON_7X"),
                        new Aeronef("FALCON_8X")
                );

                repository.saveAll(aeronefs);
            }
        };
    }
}