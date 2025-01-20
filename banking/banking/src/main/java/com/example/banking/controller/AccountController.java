package com.example.banking.controller;

import com.example.banking.dto.AccountDto;
import com.example.banking.dto.JwtResponse;
import com.example.banking.dto.RefreshTokenRequest;
import com.example.banking.entity.RefreshToken;
import com.example.banking.service.AccountService;
import com.example.banking.service.JWTService;
import com.example.banking.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final RefreshTokenService refreshTokenService;

    public AccountController(AccountService accountService, RefreshTokenService refreshTokenService) {
        this.accountService = accountService;
        this.refreshTokenService = refreshTokenService;
    }

    @Autowired
    private JWTService jwtService;

    //Add Account REST Api
    @PostMapping("/register")
    public ResponseEntity<AccountDto> addAccount(@Valid @RequestBody AccountDto accountDto ){
        return new ResponseEntity<>(accountService.createAccount(accountDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody AccountDto accountDto ){
        return accountService.verify(accountDto);
    }

    //getAllAccounts
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts(){
        List<AccountDto> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    //get account rest api
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable long id ){
        AccountDto accountDto = accountService.getAccountById(id);
        return ResponseEntity.ok(accountDto);
    }

    //Deposit REST Api
    @PutMapping("/{id}/deposit")
    public ResponseEntity<AccountDto> deposit(@PathVariable Long id, @RequestBody Map<String, Double> request) {
        AccountDto accountDto = accountService.deposit(id, request);
        return ResponseEntity.ok(accountDto);
    }


    //withdraw restApi
    @PutMapping("/{id}/withdraw")
    public ResponseEntity<AccountDto> withDraw(@PathVariable Long id, @RequestBody Map<String, Double> request) {
        AccountDto accountDto = accountService.withDraw(id, request);
        return ResponseEntity.ok(accountDto);
    }

    //delete account
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccountById(@PathVariable long id){
        accountService.deleteAccount(id);
        return ResponseEntity.ok("Account deleted");
    }


    // Secure Deposit REST API
    @PutMapping("/{id}/secure-deposit")
    public ResponseEntity<AccountDto> secureDeposit(@PathVariable Long id,
                                                    @RequestBody Map<String, Double> request,
                                                    Authentication authentication) {
        AccountDto accountDto = accountService.secureDeposit(id, request, authentication);
        return ResponseEntity.ok(accountDto);
    }


    // Secure Withdraw REST API
    @PutMapping("/{id}/secure-withdraw")
    public ResponseEntity<AccountDto> secureWithdraw(@PathVariable Long id,
                                                     @RequestBody Map<String, Double> request,
                                                     Authentication authentication) {
        AccountDto accountDto = accountService.secureWithdraw(id, request, authentication);
        return ResponseEntity.ok(accountDto);
    }


    //For finding accounts with sorting
    @GetMapping("/sort/{field}")
    public ResponseEntity<List<AccountDto>> getAccountsWithSorting(@PathVariable String field){
        List<AccountDto> allAccounts = accountService.findAccountsWithSorting(field);
        return ResponseEntity.ok(allAccounts);
    }

    //for pagination
    @GetMapping("/pagination/{offset}/{pageSize}")
    public ResponseEntity<List<AccountDto>> findAccountsWithPagination(@PathVariable int offset, @PathVariable int pageSize) {
        List<AccountDto> accounts = accountService.findAccountsWithPagination(offset, pageSize);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/pagination/sort/{offset}/{pageSize}/{field}")
    public ResponseEntity<List<AccountDto>> findAccountsWithPaginationAndSorting(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field) {
        List<AccountDto> accounts = accountService.findAccountsWithPaginationAndSorting(offset, pageSize,field);
        return ResponseEntity.ok(accounts);
    }

    //RefreshToken
    @PostMapping("/refreshToken")
    public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenService.generateNewAccessToken(refreshTokenRequest);
    }

    @PostMapping("/withoutCompression")
    public ResponseEntity<String> withoutCompression(@RequestBody String body) {
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @PostMapping("/compression")
    public ResponseEntity<byte[]> compression(@RequestBody String body) {
        return accountService.prepareCompressed(body);
    }
    
}
