package com.example.circuitbreakerbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@SpringBootApplication
public class CircuitBreakerBookApplication {

    @GetMapping("/to-read")
    public Mono<String> toRead() {
        return WebClient.builder().build()
                .get().uri("http://localhost:8090/recommended").retrieve()
                .bodyToMono(String.class);
    }

	public static void main(String[] args) {
		SpringApplication.run(CircuitBreakerBookApplication.class, args);
	}

}
