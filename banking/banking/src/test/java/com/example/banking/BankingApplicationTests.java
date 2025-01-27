package com.example.banking;

import com.example.banking.dto.AccountDto;
import com.example.banking.entity.Account;
import com.example.banking.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class BankingApplicationTests extends AbstractContainerBaseTest{

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AccountRepository accountRepository;

	@Test
	void testCreateAccount() throws Exception {

		AccountDto accountDto = new AccountDto(
				0L,  // ID will be auto-generated
				"John Doe",
				1000.00,
				LocalDateTime.now(),
				"securePassword",
				null // Address can be null for this test
		);

		// Serialize the DTO to JSON
		String accountJson = objectMapper.writeValueAsString(accountDto);

		// Perform a POST request to the /api/accounts/register endpoint
		mockMvc.perform(post("/api/accounts/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(accountJson))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id", notNullValue()))  // ID should be auto-generated
				.andExpect(jsonPath("$.accountHolderName", is("John Doe")))
				.andExpect(jsonPath("$.balance", is(1000.00)))
				.andExpect(jsonPath("$.password", notNullValue())); // Ensure the password is set

		// Verify the account was saved in the database
		Optional<Account> savedAccount = Optional.ofNullable(accountRepository.findByAccountHolderName("John Doe"));
		assertTrue(savedAccount.isPresent(), "Account should exist in the database");
		assertEquals("John Doe", savedAccount.get().getAccountHolderName());
		assertEquals(1000.00, savedAccount.get().getBalance());
	}

}
