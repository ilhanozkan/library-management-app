package com.ilhanozkan.libraryManagementSystem.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
  private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
  
  @Value("${jwt.secret}")
  private String secretKey;
  @Value("${jwt.expiration}")
  private long jwtExpiration;

  public SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  public Claims extractAllClaims(String token) {
    try {
      return Jwts
          .parser()
          .verifyWith(getSecretKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (Exception e) {
      logger.error("Error extracting claims from token: {}", e.getMessage(), e);
      throw e;
    }
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String extractUsername(String token) {
    try {
      return extractClaim(token, Claims::getSubject);
    } catch (Exception e) {
      logger.error("Error extracting username from token: {}", e.getMessage(), e);
      throw e;
    }
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    try {
      final String username = extractUsername(token);
      return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    } catch (RuntimeException e) {
      logger.error("Token validation failed: {}", e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  public String generateToken(String username) {
    Map<String, Object> claims = new HashMap<>();
    return Jwts
        .builder()
        .claims()
        .add(claims)
        .subject(username)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .and()
        .signWith(getSecretKey())
        .compact();
  }
}
