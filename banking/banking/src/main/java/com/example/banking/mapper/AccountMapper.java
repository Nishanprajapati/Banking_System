package com.example.banking.mapper;

import com.example.banking.dto.AccountDto;
import com.example.banking.entity.Account;

public class AccountMapper {
    public static Account mapToAccount(AccountDto accountDto) {
        Account account = new Account(
                accountDto.getId(),
                accountDto.getAccountHolderName(),
                accountDto.getBalance(),
                accountDto.getCreatedAt(),
                accountDto.getPassword(),
                accountDto.getAddress()
        );
        return account;
    }

    public static AccountDto mapToAccountDto(Account account) {
        AccountDto accountDto = new AccountDto(
                account.getId(),
                account.getAccountHolderName(),
                account.getBalance(),
                account.getCreatedAt(),
                account.getPassword(),
                account.getAddress()
        );
        return accountDto;
    }
}
