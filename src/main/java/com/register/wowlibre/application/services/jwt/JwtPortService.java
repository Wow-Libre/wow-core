package com.register.wowlibre.application.services.jwt;

import com.register.wowlibre.domain.port.in.jwt.JwtPort;
import com.register.wowlibre.domain.shared.CustomUserDetails;
import com.register.wowlibre.infrastructure.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.register.wowlibre.domain.constant.Constants.*;


@Component
@Slf4j
public class JwtPortService implements JwtPort {

    private final JwtProperties jwtProperties;

    public JwtPortService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get(HEADER_USER_ID, Long.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String generateToken(CustomUserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(CONSTANT_ROL, userDetails.getAuthorities());
        extraClaims.put(HEADER_USER_ID, userDetails.getUserId());
        extraClaims.put(HEADER_LANGUAGE, userDetails.getLanguage());

        return generateToken(extraClaims, userDetails);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtProperties.getJwtExpiration());
    }

    @Override
    public String generateRefreshToken(CustomUserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, jwtProperties.getRefreshExpiration());
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token) {
        return isSignatureValid(token) && !isTokenExpired(token);
    }

    private boolean isSignatureValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Error validating token signature: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public Collection<GrantedAuthority> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        List<Map<String, String>> rolesAsMap = getRolesFromClaims(claims);
        return rolesAsMap.stream()
                .map(this::convertToAuthority)
                .collect(Collectors.toList());
    }

    private List<Map<String, String>> getRolesFromClaims(Claims claims) {
        return Optional.ofNullable(claims.get(CONSTANT_ROL))
                .filter(List.class::isInstance)
                .map(List.class::cast)
                .orElseGet(Collections::emptyList);
    }

    private GrantedAuthority convertToAuthority(Map<String, String> roleMap) {
        String authority = roleMap.get("authority");
        return new SimpleGrantedAuthority(authority);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
