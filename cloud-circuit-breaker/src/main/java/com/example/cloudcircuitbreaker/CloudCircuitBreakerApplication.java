package com.example.cloudcircuitbreaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@SpringBootApplication
public class CloudCircuitBreakerApplication {

	@GetMapping("/recommended")
	public Mono<String> readingList() {
		return Mono.just("Spring in Action (Manning), Cloud Native Java (O'Reilly), Learning Spring Boot (Packt)");
	}

	public static void main(String[] args) {
		SpringApplication.run(CloudCircuitBreakerApplication.class, args);
	}

}
