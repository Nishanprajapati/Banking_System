package com.example.banking.service;

import com.example.banking.dto.JwtResponse;
import com.example.banking.dto.RefreshTokenRequest;
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

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountRepository accountRepository;
    private final JWTService jwtService;
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, AccountRepository accountRepository, JWTService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.accountRepository = accountRepository;
        this.jwtService = jwtService;
    }



    public JwtResponse generateNewAccessToken(RefreshTokenRequest refreshTokenRequest) {
        return findByToken(refreshTokenRequest.getToken())
                .map(this::verifyExpiration)
                .map(RefreshToken::getAccount)
                .map(account -> {
                    String accessToken = jwtService.generateToken(account.getAccountHolderName());
                    return JwtResponse.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequest.getToken())
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token could not be generated"));
    }

    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken=RefreshToken.builder()
                .account(accountRepository.findByAccountHolderName(username))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(6000000))
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

