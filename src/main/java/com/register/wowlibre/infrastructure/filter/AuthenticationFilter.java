package com.register.wowlibre.infrastructure.filter;


import com.fasterxml.jackson.databind.*;
import com.register.wowlibre.domain.constant.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.port.in.jwt.*;
import com.register.wowlibre.domain.security.*;
import com.register.wowlibre.domain.shared.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.apache.logging.log4j.*;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.*;

import java.io.*;
import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String PATH_AUTHENTICATION_FILTER = "/api/auth/login";
    private final AuthenticationManager authenticationManager;
    private final JwtPort jwtPort;

    public AuthenticationFilter(AuthenticationManager authenticationManager, JwtPort jwtPort) {
        this.authenticationManager = authenticationManager;
        this.jwtPort = jwtPort;
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return "POST".equalsIgnoreCase(request.getMethod()) &&
                PATH_AUTHENTICATION_FILTER.equals(request.getServletPath());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String transactionId = request.getHeader(Constants.HEADER_TRANSACTION_ID);
        ThreadContext.put(CONSTANT_UNIQUE_ID, transactionId);
        UserLoginModel user = getParamsUser(request, transactionId);
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.username, user.password)
        );
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException {

        String transactionId = request.getHeader(Constants.HEADER_TRANSACTION_ID);

        Map<String, Object> body = new HashMap<>();
        ThreadContext.put(CONSTANT_UNIQUE_ID, transactionId);

        try {
            final JwtDto jwt = generateToken(authResult);
            body.put("message", "ok");
            body.put("code", 200);
            body.put("data", jwt);
            body.put(Constants.HEADER_TRANSACTION_ID, transactionId);

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(200);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        } catch (Exception e) {
            body.put("error", "invalid data");
            body.put("message", Constants.Errors.CONSTANT_GENERIC_ERROR_MESSAGE);
            body.put("message_trace", e.getMessage());
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(401);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        }

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Please verify the information provided.");
        body.put("message", Constants.Errors.CONSTANT_GENERIC_ERROR_MESSAGE);
        body.put("message_trace", failed.getMessage());

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    }


    private UserLoginModel getParamsUser(HttpServletRequest request, String transactionId) {
        try {
            return new ObjectMapper().readValue(request.getInputStream(), UserLoginModel.class);
        } catch (IOException e) {
            logger.error("Invalid parameters, please check your information");
            throw new UnauthorizedException(Constants.Errors.CONSTANT_GENERIC_ERROR_MESSAGE, transactionId);
        }
    }

    private JwtDto generateToken(Authentication authResult) {
        CustomUserDetails customUserDetails = ((CustomUserDetails) authResult.getPrincipal());
        String token = jwtPort.generateToken(customUserDetails);
        Date expiration = jwtPort.extractExpiration(token);
        String refreshToken = jwtPort.generateRefreshToken(customUserDetails);
        return new JwtDto(customUserDetails.getUserId(), token, refreshToken, expiration,
                customUserDetails.getAvatarUrl(), customUserDetails.getLanguage(),
                customUserDetails.isPendingValidation(), customUserDetails.isAdmin());
    }
}
