package com.example.banking.service.impl;

import com.example.banking.dto.AccountDto;
import com.example.banking.entity.Account;
import com.example.banking.exception.AccountNotFoundException;
import com.example.banking.mapper.AccountMapper;
import com.example.banking.repository.AccountRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    private AccountServiceImpl accountServiceImpl;

    private BCryptPasswordEncoder encoder;
    private static MockedStatic<AccountMapper> mockedMapper;


    @BeforeEach
    void setUp() {
        encoder = new BCryptPasswordEncoder(12);
        accountServiceImpl = new AccountServiceImpl(accountRepository);
        mockedMapper = mockStatic(AccountMapper.class);
    }

    @AfterEach
    void tearDown() {
        mockedMapper.close();
    }

    @Test
    void getAllAccounts() {
        accountServiceImpl.getAllAccounts();
        verify(accountRepository).findAll();
    }

    @Test
    void createAccount() {
        AccountDto inputDto = new AccountDto(0L, "Nishan", 1000.0, null, "Password", null);
        Account mappedAccount = new Account(0L, "Nishan", 1000.0, LocalDateTime.now(), "Password", null);
        Account savedAccount = new Account(1L, "Nishan", 1000.0, LocalDateTime.now(), "Password", null);
        AccountDto expectedDto = new AccountDto(1L, "Nishan", 1000.0, LocalDateTime.now(), "Password", null);

        mockedMapper.when(() -> AccountMapper.mapToAccount(inputDto)).thenReturn(mappedAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        mockedMapper.when(() -> AccountMapper.mapToAccountDto(savedAccount)).thenReturn(expectedDto);

        AccountDto result = accountServiceImpl.createAccount(inputDto);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getAccountHolderName(), result.getAccountHolderName());
        assertEquals(expectedDto.getBalance(), result.getBalance());
        assertEquals(expectedDto.getPassword(), result.getPassword());

        verify(accountRepository).save(mappedAccount);
    }



    @Test
    void deleteAccount() {
        Long accountId = 1L;
        Account account = new Account(accountId, "Nishan", 1000.0, LocalDateTime.now(), "Password", null);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        accountServiceImpl.deleteAccount(accountId);
        verify(accountRepository).findById(accountId);
        verify(accountRepository).deleteById(accountId);

    }

    @Test
    void deposit() {
        Long accountId = 1L;
        Map<String, Double> request = new HashMap<>();
        request.put("amount", 500.0);
        double depositAmount = 500.0;

        Account account = new Account(accountId, "Nishan", 1000.0, LocalDateTime.now(), "encodedPassword", null);
        Account updatedAccount = new Account(accountId, "Nishan", 1500.0, LocalDateTime.now(), "encodedPassword", null);
        AccountDto expectedDto = new AccountDto(accountId, "Nishan", 1500.0, LocalDateTime.now(), "encodedPassword", null);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);
        mockedMapper.when(() -> AccountMapper.mapToAccountDto(updatedAccount)).thenReturn(expectedDto);


        AccountDto result = accountServiceImpl.deposit(accountId, request);

        assertNotNull(result);
        assertEquals(1500.0, result.getBalance());
        assertEquals("Nishan", result.getAccountHolderName());

        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(any(Account.class));
        verify(accountRepository).save(updatedAccount);
    }


    @Test
    void withDraw() {
        Long accountId = 1L;
        Map<String, Double> request = new HashMap<>();
        request.put("amount", 500.0);
        double withdrawAmount = 500.0;

        Account account = new Account(accountId, "Nishan", 1000.0, LocalDateTime.now(), "encodedPassword", null);
        Account updatedAccount = new Account(accountId, "Nishan", 500.0, LocalDateTime.now(), "encodedPassword", null);
        AccountDto expectedDto = new AccountDto(accountId, "Nishan", 500.0, LocalDateTime.now(), "encodedPassword", null);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);
        mockedMapper.when(() -> AccountMapper.mapToAccountDto(updatedAccount)).thenReturn(expectedDto);


        AccountDto result = accountServiceImpl.withDraw(accountId, request);

        assertNotNull(result);
        assertEquals(500.0, result.getBalance());
        assertEquals("Nishan", result.getAccountHolderName());

        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(any(Account.class));
        verify(accountRepository).save(updatedAccount);
    }

    @Test
    void secureWithdraw() {
        Long accountId = 1L;
        Map<String, Double> request = new HashMap<>();
        request.put("amount", 500.0);
        String username = "Nishan";

        Account account = new Account(accountId, "Nishan", 1000.0, LocalDateTime.now(), "encodedPassword", null);
        Account updatedAccount = new Account(accountId, "Nishan", 500.0, LocalDateTime.now(), "encodedPassword", null);
        AccountDto expectedDto = new AccountDto(accountId, "Nishan", 500.0, LocalDateTime.now(), "encodedPassword", null);

        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);

        when(authentication.getName()).thenReturn(username);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);
        mockedMapper.when(() -> AccountMapper.mapToAccountDto(updatedAccount)).thenReturn(expectedDto);

        AccountDto result = accountServiceImpl.secureWithdraw(accountId, request, authentication);

        assertNotNull(result);
        assertEquals(500.0, result.getBalance());
        assertEquals("Nishan", result.getAccountHolderName());

        verify(authentication).getName();
        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void secureDeposit() {
        Long accountId = 1L;
        Map<String, Double> request = new HashMap<>();
        request.put("amount", 500.0);
        String username = "Nishan";

        Account account = new Account(accountId, "Nishan", 1000.0, LocalDateTime.now(), "encodedPassword", null);
        Account updatedAccount = new Account(accountId, "Nishan", 1500.0, LocalDateTime.now(), "encodedPassword", null);
        AccountDto expectedDto = new AccountDto(accountId, "Nishan", 1500.0, LocalDateTime.now(), "encodedPassword", null);

        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);

        when(authentication.getName()).thenReturn(username);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);
        mockedMapper.when(() -> AccountMapper.mapToAccountDto(updatedAccount)).thenReturn(expectedDto);

        AccountDto result = accountServiceImpl.secureDeposit(accountId, request, authentication);

        assertNotNull(result);
        assertEquals(1500.0, result.getBalance());
        assertEquals("Nishan", result.getAccountHolderName());

        verify(authentication).getName();
        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(any(Account.class));
    }

    //for exceptional handling gor getAccountById
    @Test
    void getAccountById_ThrowsAccountNotFoundException() {
        // Mock the behavior of accountRepository.findById to return empty Optional indicating no account id=1
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // check the method getAccountById(1l) throws AccountNotFoundException
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            accountServiceImpl.getAccountById(1L);
        });

        // Verify that the exception message is correct
        assertEquals("Account does not exist", exception.getMessage());

        // Verify that the repository method was called
        verify(accountRepository).findById(1L);
    }

    //    @Test
//    void getAccountById() {
//        //  mock data
//        Address testAddress = new Address(1L, "New York", "Home");
//        Account testAccount = new Account(1L, "John Doe", 1000.0, LocalDateTime.now(), "securePassword", testAddress);
//
//        // Step 2: Mock the behavior of accountRepository.findById when called with id 1L and return testAccount
//        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
//
//        // Step 3: Call the method under test
//        AccountDto accountDto = accountServiceImpl.getAccountById(1L);
//
//        // Step 4: Verify repository interaction
//        verify(accountRepository).findById(1L);
//
//        // Step 5: Assert that the returned AccountDto matches the Account entity's fields
//        assertNotNull(accountDto);  // Ensure accountDto is not null
//        assertEquals(testAccount.getId(), accountDto.getId());  // Check ID
//        assertEquals(testAccount.getAccountHolderName(), accountDto.getAccountHolderName());  // Check account holder name
//        assertEquals(testAccount.getBalance(), accountDto.getBalance());  // Check balance
//        assertEquals(testAccount.getPassword(), accountDto.getPassword());  // Check password
//        assertNotNull(accountDto.getAddress());  // Ensure the address is not null
//        assertEquals(testAccount.getAddress().getCity(), accountDto.getAddress().getCity());  // Check city
//        assertEquals(testAccount.getAddress().getAddressType(), accountDto.getAddress().getAddressType());  // Check address type
//    }



}









