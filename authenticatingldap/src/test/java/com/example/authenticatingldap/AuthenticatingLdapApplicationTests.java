package com.example.authenticatingldap;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

/**
 *
 * @author Rob Winch
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(WebSecurityConfig.class)
public class AuthenticatingLdapApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void loginWithValidUserThenAuthenticated() throws Exception {
		FormLoginRequestBuilder login = formLogin()
				.user("ben")
				.password("benspassword");

		mockMvc.perform(login)
				.andExpect(authenticated().withUsername("ben"));
	}

	@Test
	public void loginWithInvalidUserThenUnauthenticated() throws Exception {
		FormLoginRequestBuilder login = formLogin()
				.user("invalid")
				.password("invalidpassword");

		mockMvc.perform(login)
				.andExpect(unauthenticated());
	}
}
