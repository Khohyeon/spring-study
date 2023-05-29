package com.example.cassandra;

import com.example.cassandra.entity.Vet;
import com.example.cassandra.repository.VetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableCassandraRepositories
public class CassandraApplication {

    public static void main(String[] args) {
        SpringApplication.run(CassandraApplication.class, args);
    }

    private final static Logger log = LoggerFactory.getLogger(CassandraApplication.class);

    @Bean
    public CommandLineRunner clr(VetRepository vetRepository) {
        return args -> {

            Vet jinhui = new Vet(UUID.randomUUID(),
                    "Jinhui",
                    "Park",
                    new HashSet<>(List.of("surgery")));

            Vet hohyeon = new Vet(UUID.randomUUID(),
                    "Jinhui",
                    "Park",
                    new HashSet<>(List.of("radiology", "surgery")));

            Vet saveJinhui = vetRepository.save(jinhui);
            Vet saveHohyeon = vetRepository.save(hohyeon);

            vetRepository.findAll()
                            .forEach(vet -> {
                                log.info("Vet: {}", vet.getFirstName());
                            });

            vetRepository.findById(saveJinhui.getId())
                    .ifPresent(vet -> {
                        log.info("Vet by id: {}", vet.getFirstName());
                    });
        };
    }
}
