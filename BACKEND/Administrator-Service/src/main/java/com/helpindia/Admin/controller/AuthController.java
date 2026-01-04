package com.helpindia.Admin.controller;

import com.helpindia.Admin.DTOs.AuthResponse;
import com.helpindia.Admin.DTOs.RefreshTokenRequest;
import com.helpindia.Admin.JWT.JWTService;
import com.helpindia.Admin.model.RefreshToken;
import com.helpindia.Admin.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController
{
    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthController(JWTService jwtService, RefreshTokenService refreshTokenService)
    {
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshTokenRequest refreshTokenRequest)
    {
        RefreshToken storedToken = refreshTokenService.verify(refreshTokenRequest.refreshToken());

        String newAccessToken = jwtService.generateToken(storedToken.getUsername());
        String newRefreshToken = jwtService.generateRefreshToken(storedToken.getUsername());

        refreshTokenService.revoke(storedToken);
        refreshTokenService.create(storedToken.getUsername(), newRefreshToken);

        return new AuthResponse(newAccessToken, newRefreshToken, "Bearer");
    }

    @PostMapping("/logout")
    public void logout(@RequestBody RefreshTokenRequest refreshTokenRequest)
    {
        RefreshToken storedToken = refreshTokenService.verify(refreshTokenRequest.refreshToken());
        refreshTokenService.revoke(storedToken);

    }
}
