package com.register.wowlibre.application.services.telegram;

import com.register.wowlibre.domain.port.in.jwt.JwtPort;
import com.register.wowlibre.domain.security.JwtDto;
import com.register.wowlibre.domain.shared.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Servicio de autenticación para el bot de Telegram.
 * Reutiliza el mismo flujo de la plataforma: email (username) + contraseña,
 * validación contra UserDetailsService y generación de JWT.
 */
@Service
public class TelegramAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtPort jwtPort;

    public TelegramAuthService(AuthenticationManager authenticationManager, JwtPort jwtPort) {
        this.authenticationManager = authenticationManager;
        this.jwtPort = jwtPort;
    }

    /**
     * Autentica con correo y contraseña de la plataforma.
     * Lanza BadCredentialsException si las credenciales son inválidas.
     */
    public JwtDto authenticate(String email, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String token = jwtPort.generateToken(userDetails);
        Date expiration = jwtPort.extractExpiration(token);
        String refreshToken = jwtPort.generateRefreshToken(userDetails);
        return new JwtDto(
                userDetails.getUserId(),
                token,
                refreshToken,
                expiration,
                userDetails.getAvatarUrl(),
                userDetails.getLanguage(),
                userDetails.isPendingValidation(),
                userDetails.isAdmin()
        );
    }
}
