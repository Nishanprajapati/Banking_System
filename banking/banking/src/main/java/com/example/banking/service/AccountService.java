package com.example.banking.service;

import com.example.banking.dto.AccountDto;
import com.example.banking.dto.JwtResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

public interface AccountService {

    AccountDto createAccount(AccountDto account);

    AccountDto getAccountById(Long id);

    AccountDto deposit(Long id, Map<String, Double> request);

    AccountDto withDraw(Long id, Map<String, Double> request);

    List<AccountDto> getAllAccounts();

    void deleteAccount(Long id);

    AccountDto secureDeposit(Long id, Map<String, Double> request, Authentication authentication);

    AccountDto secureWithdraw(Long id, Map<String, Double> request, Authentication authentication);

    List<AccountDto> findAccountsWithSorting(String field);

    List<AccountDto> findAccountsWithPagination(int offset, int pageSize);

    List<AccountDto> findAccountsWithPaginationAndSorting(int offset, int pageSize,String field);

    JwtResponse verify(AccountDto accountDto);

    byte[] compress(byte[] data);

    ResponseEntity<byte[]> prepareCompressed(String body);
}
