package com.example.contractrestservice;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = ContractRestServiceApplication.class)
public abstract class BaseClass {

    @MockBean
    PersonService personService;

    private final PersonRestController personRestController;

    protected BaseClass(PersonRestController personRestController) {
        this.personRestController = personRestController;
    }

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.standaloneSetup(personRestController);

        Mockito.when(personService.findPersonById(1L))
                .thenReturn(new Person(1L, "foo", "bee"));
    }

}