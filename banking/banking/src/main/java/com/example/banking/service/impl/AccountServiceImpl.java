package com.example.banking.service.impl;

import com.example.banking.dto.AccountDto;
import com.example.banking.dto.JwtResponse;
import com.example.banking.entity.Account;
import com.example.banking.entity.RefreshToken;
import com.example.banking.exception.AccountNotFoundException;
import com.example.banking.mapper.AccountMapper;
import com.example.banking.repository.AccountRepository;
import com.example.banking.service.AccountService;
import com.example.banking.service.JWTService;
import com.example.banking.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;


@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AuthenticationManager authManager;
    private final RefreshTokenService refreshTokenService;
    private final JWTService jwtService;
    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              AuthenticationManager authManager,
                              JWTService jwtService,
                              RefreshTokenService refreshTokenService) {
        this.accountRepository = accountRepository;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    // Delegate constructor for AccountRepository for test
    public AccountServiceImpl(AccountRepository accountRepository) {
        this(accountRepository, null, null, null); // Delegate to the primary constructor
    }


    private final  BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
//        Optional<Account> existingAccount = Optional.ofNullable(accountRepository.findByAccountHolderName(accountDto.getAccountHolderName()));
//        if (existingAccount.isPresent()) {
//            throw new DuplicateAccountHolderNameException("Account holder name already exists: " + accountDto.getAccountHolderName());
//        }
        Account account = AccountMapper.mapToAccount(accountDto);
        account.setPassword(encoder.encode(account.getPassword()));
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }



    @Override
    public AccountDto getAccountById(Long id){
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account does not exist"));
        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, Map<String, Double> request) {
        // Extract the amount here
        Double amount = request.get("amount");

        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        double total = account.getBalance() + amount;
        account.setBalance(total);

        Account savedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(savedAccount);
    }



    @Override
    public AccountDto withDraw(Long id, Map<String, Double> request) {
        // Extract the amount here
        Double amount = request.get("amount");

        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (account.getBalance() < amount) {
            throw new AccountNotFoundException("Insufficient balance");
        }

        double total = account.getBalance() - amount;
        account.setBalance(total);

        Account savedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(savedAccount);
    }



    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        accountRepository.deleteById(id);
    }


    @Override
    public AccountDto secureDeposit(Long id, Map<String, Double> request, Authentication authentication) {
        Double amount = request.get("amount");
        String username = authentication.getName();

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (!account.getAccountHolderName().equals(username)) {
            throw new AccountNotFoundException("Access denied: You can only deposit to your own account.");
        }

        account.setBalance(account.getBalance() + amount);
        Account savedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(savedAccount);
    }



    @Override
    public AccountDto secureWithdraw(Long id, Map<String, Double> request, Authentication authentication) {
        Double amount = request.get("amount");
        String username = authentication.getName();

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (!account.getAccountHolderName().equals(username)) {
            throw new AccountNotFoundException("Access denied: You can only withdraw from your own account.");
        }

        if (account.getBalance() < amount) {
            throw new AccountNotFoundException("Insufficient balance");
        }

        account.setBalance(account.getBalance() - amount);
        Account savedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(savedAccount);
    }



    @Override
    public List<AccountDto> getAllAccounts() {
        List<Account> accounts=accountRepository.findAll();
        return accounts.stream().map(AccountMapper::mapToAccountDto).collect(Collectors.toList());
    }

    @Override
    public List<AccountDto> findAccountsWithSorting(String field) {
        List<Account> accounts = accountRepository.findAll(Sort.by(Sort.Direction.ASC,field));
        return accounts.stream().map(AccountMapper::mapToAccountDto).collect(Collectors.toList());
    }

    @Override
    public List<AccountDto> findAccountsWithPagination(int offset, int pageSize) {
        Page<Account> accounts = accountRepository.findAll(PageRequest.of(offset, pageSize));
        return accounts.getContent().stream().map(AccountMapper::mapToAccountDto).collect(Collectors.toList());
    }

    @Override
    public List<AccountDto> findAccountsWithPaginationAndSorting(int offset, int pageSize, String field) {
        Page<Account> accounts = accountRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.Direction.ASC,field));
        return accounts.getContent().stream().map(AccountMapper::mapToAccountDto).collect(Collectors.toList());
    }

    @Override
    public JwtResponse verify(AccountDto accountDto) {
        Authentication authentication= authManager.authenticate(new UsernamePasswordAuthenticationToken(accountDto.getAccountHolderName(),accountDto.getPassword()));

        if(authentication.isAuthenticated()) {
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(accountDto.getAccountHolderName());
            return JwtResponse.builder()
                    .accessToken(jwtService.generateToken(accountDto.getAccountHolderName()))
                    .token(refreshToken.getToken()).build();
        } else{
            throw new AccountNotFoundException("Account does not exist");
        }

    }

    @Override
    public byte[] compress(byte[] data) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(data);
            gzipOutputStream.finish();
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during compression", e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public ResponseEntity<byte[]> prepareCompressed(String body) {
        byte[] compressedResponse = compress(body.getBytes());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Encoding", "gzip");

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(compressedResponse);
    }


}
