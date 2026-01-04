package com.helpindia.Admin.JWT;

import com.helpindia.Admin.Exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JWTService
{
    @Value("${jwt.secret}")
    private String secretKey = "";

    private static final long expirationInterval = 1000 * 60 * 5;

    private SecretKey getSigninKey()
    {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username)
    {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationInterval))
                .signWith(getSigninKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(String username)
    {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 7)))
                .signWith(getSigninKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String extractUsername(String token)
    {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) throws InvalidTokenException {
//        String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));

        try
        {
            String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }
        catch (Exception e)
        {
            throw new InvalidTokenException("Invalid Token.");
        }
    }

    public boolean isTokenExpired(String token)
    {
        return extractExpirationDate(token).before(new Date());
    }

    public Date extractExpirationDate(String token)
    {
        return extractClaims(token).getExpiration();
    }

}
