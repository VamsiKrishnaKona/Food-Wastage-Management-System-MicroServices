package com.helpindia.Admin.service;

import com.helpindia.Admin.Repository.RefreshTokenRepository;
import com.helpindia.Admin.model.RefreshToken;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenService
{
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository)
    {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void create(String username, String refreshToken)
    {
        RefreshToken rt = new RefreshToken();

        rt.setUsername(username);
        rt.setToken(refreshToken);
        rt.setExpirationDate(Instant.now().plusSeconds(7 * 24 * 60 * 60));
        refreshTokenRepository.save(rt);
    }

    public RefreshToken verify(String refreshToken)
    {
        RefreshToken rt = refreshTokenRepository.findByToken(refreshToken).orElseThrow(() -> new RuntimeException("Invalid refresh Token"));

        if(rt.isRevoked() || rt.getExpirationDate().isBefore(Instant.now()))
        {
            throw new RuntimeException("Refresh Token expired or revoked");
        }
        return rt;
    }

    public void revoke(RefreshToken refreshToken)
    {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}
