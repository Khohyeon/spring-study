package com.example.metrics_tracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportRuntimeHints;

@ImportRuntimeHints(PetClinicRuntimeHints.class)
@SpringBootApplication
public class MetricsTracingApplication {
	public static void main(String[] args) {
		SpringApplication.run(MetricsTracingApplication.class, args);
	}
}

