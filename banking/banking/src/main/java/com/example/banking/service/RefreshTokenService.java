package com.example.banking.service;

import com.example.banking.entity.RefreshToken;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AccountRepository accountRepository;

    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken=RefreshToken.builder()
                .account(accountRepository.findByAccountHolderName(username))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000))
                .build();
       return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token.getExpiryDate().compareTo(Instant.now())<0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken()+" is expired.Please make a new sign in request");
        }
        return token;
    }
}

