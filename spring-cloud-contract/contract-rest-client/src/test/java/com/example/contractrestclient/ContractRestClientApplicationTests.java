package com.example.contractrestclient;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest
@AutoConfigureStubRunner(
		ids = "com.example:contract-rest-service:0.0.1-SNAPSHOT:stubs:8100",
		stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
class ContractRestClientApplicationTests {

	@Test
	public void get_person_from_service_contract() {
		// given
		RestTemplate restTemplate = new RestTemplate();

		// when
		ResponseEntity<Person> personResponseEntity = restTemplate.getForEntity("http://localhost:8100/person/1", Person.class);

		// then
		then(personResponseEntity.getStatusCode()).isEqualTo(200);
		then(personResponseEntity.getBody().getId()).isEqualTo(1L);
		then(personResponseEntity.getBody().getName()).isEqualTo("foo");
		then(personResponseEntity.getBody().getSurname()).isEqualTo("bee");
	}

}
