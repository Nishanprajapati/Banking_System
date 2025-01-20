package com.example.banking;

import com.example.banking.dto.AccountDto;
import com.example.banking.entity.Address;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BankingApplicationTests {

	@LocalServerPort
	private int port;

	private String baseUrl="http://localhost";

	private static RestTemplate restTemplate;

	private H2Repository h2Repository;

	@BeforeAll
	public static void init(){
		restTemplate = new RestTemplate();
	}

	@BeforeEach
	public void setup(){
		baseUrl=baseUrl.concat(":").concat(port+"").concat("/api/accounts");
	}

	@Test
	void contextLoads() {
	}

	@Test
	public void testAddAccount() {
		Address address = new Address();
		address.setCity("New York");
		address.setAddressType("Home");

		AccountDto accountDto = new AccountDto(
				0L,
				"Nishan",
				1000.0,
				LocalDateTime.now(),
				"123",
				address
		);

		ResponseEntity<AccountDto> response = restTemplate.postForEntity(baseUrl + "/register", accountDto, AccountDto.class);


		assertNotNull(response);

	}





}
