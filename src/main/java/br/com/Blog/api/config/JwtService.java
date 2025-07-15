package br.com.Blog.api.config;

import br.com.Blog.api.entities.Role;
import br.com.Blog.api.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtService  {

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    @Value("${app.jwt.expiration}")
    private long EXPIRETION_ACCESS_TOKEN;

    @Value("${app.jwt.expiration.refresh_token}")
    private long EXPIRATION_REFRESH_TOKEN;

    @Deprecated
    public String generateToken(UserDetails userDetails, Long id) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("userId", id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + this.EXPIRETION_ACCESS_TOKEN ))
                .signWith(getKey(), SignatureAlgorithm.HS384)
                .compact().trim();
    }

    public String generateTokenV2(UserDetails userDetails, User user) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + this.EXPIRETION_ACCESS_TOKEN ))
                .signWith(getKey(), SignatureAlgorithm.HS384)
                .compact().trim();
    }

    public String generateRefreshtokenv2(UserDetails userDetails, User user) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + this.EXPIRATION_REFRESH_TOKEN ))
                .signWith(getKey(), SignatureAlgorithm.HS384)
                .compact().trim();
    }

    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    @Deprecated
    public String generateRefreshtoken(UserDetails userDetails, Long id) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("userId", id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + this.EXPIRATION_REFRESH_TOKEN ))
                .signWith(getKey(), SignatureAlgorithm.HS384)
                .compact().trim();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Long extractId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        return extractUserId(token);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpiredV2(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().before(new Date());
    }


    public boolean isTokenExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration().before(new Date());
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

}
