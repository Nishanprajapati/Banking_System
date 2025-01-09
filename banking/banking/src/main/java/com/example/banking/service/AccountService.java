package com.example.banking.service;

import com.example.banking.dto.AccountDto;
import com.example.banking.dto.JwtResponse;

import java.util.List;

public interface AccountService {

    AccountDto createAccount(AccountDto account);

    AccountDto getAccountById(Long id);

    AccountDto deposit(Long id, double amount);

    AccountDto withDraw(Long id, double amount);

    List<AccountDto> getAllAccounts();

    void deleteAccount(Long id);

    AccountDto secureDeposit(Long id, double amount, String username);

    AccountDto secureWithdraw(Long id, double amount, String username);

    List<AccountDto> findAccountsWithSorting(String field);

    List<AccountDto> findAccountsWithPagination(int offset, int pageSize);

    List<AccountDto> findAccountsWithPaginationAndSorting(int offset, int pageSize,String field);

    JwtResponse verify(AccountDto accountDto);

     byte[] compress(byte[] data);
}
