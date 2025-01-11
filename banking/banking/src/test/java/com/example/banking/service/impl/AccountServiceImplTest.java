package com.example.banking.service.impl;

import com.example.banking.dto.AccountDto;
import com.example.banking.entity.Account;
import com.example.banking.entity.Address;
import com.example.banking.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    private AccountServiceImpl accountServiceImpl;

    @BeforeEach
    void setUp() {
        this.accountServiceImpl = new AccountServiceImpl(this.accountRepository);
    }

    @Test
    void getAllAccounts() {
        accountServiceImpl.getAllAccounts();
        verify(accountRepository).findAll();
    }

    @Test
    void getAccountById(){

        // Step 1: Prepare mock data for Account and Address
        Address testAddress = new Address(1L, "New York", "Home");
        Account testAccount = new Account(1L, "John Doe", 1000.0, LocalDateTime.now(), "securePassword", testAddress);

        // Step 2: Mock the behavior of (accountRepository.findById) when call by 1L abd return testAccount
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Step 3: Call the method under test
        AccountDto accountDto = accountServiceImpl.getAccountById(1L);

        // Step 4: Verify repository interaction
        verify(accountRepository).findById(1L);

        // Step 5: Assert that the returned AccountDto matches the Account entity's fields
        assertNotNull(accountDto);
        assertEquals(testAccount.getId(), accountDto.getId());
        assertEquals(testAccount.getAccountHolderName(), accountDto.getAccountHolderName());
        assertEquals(testAccount.getBalance(), accountDto.getBalance());
        assertEquals(testAccount.getPassword(), accountDto.getPassword());
        assertNotNull(accountDto.getAddress());
        assertEquals(testAccount.getAddress().getCity(), accountDto.getAddress().getCity());
        assertEquals(testAccount.getAddress().getAddressType(), accountDto.getAddress().getAddressType()); // Assert balance is correct
    }
}