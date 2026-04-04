package com.register.wowlibre.infrastructure.filter;

import com.fasterxml.jackson.databind.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.port.in.jwt.*;
import com.register.wowlibre.domain.shared.*;
import io.jsonwebtoken.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.apache.commons.lang3.*;
import org.apache.logging.log4j.*;
import org.slf4j.*;
import org.slf4j.Logger;
import org.springframework.http.*;
import org.springframework.lang.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.*;

import java.io.*;
import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtPort jwtPort;

    public JwtAuthenticationFilter(JwtPort jwtPort) {
        this.jwtPort = jwtPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        GenericResponse<Void> responseBody = new GenericResponse<>();
        String clientIp = request.getRemoteAddr();

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String transactionId = UUID.randomUUID().toString();

        ThreadContext.put(CONSTANT_UNIQUE_ID, transactionId);

        LOGGER.info("Request URL: [{}], Transaction ID: [{}], JWT Provided: [{}]", request.getRequestURI(),
                transactionId, authHeader != null);
        try {

            if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            final String jwt = authHeader.substring(7);
            final String email = jwtPort.extractUsername(jwt);
            final Long userId = jwtPort.extractUserId(jwt);

            /*
             * multipart/form-data: no usar RequestWrapper (lee todo el body en el constructor).
             * Si se consume el stream aquí, DispatcherServlet no puede parsear partes y MultipartFile llega vacío.
             */
            String contentType = request.getContentType();
            boolean multipart = StringUtils.isNotBlank(contentType)
                    && StringUtils.startsWithIgnoreCase(contentType, MediaType.MULTIPART_FORM_DATA_VALUE);
            HttpServletRequest requestWrapper;
            if (multipart) {
                HeaderMutatingRequestWrapper w = new HeaderMutatingRequestWrapper(request);
                w.setHeader(HEADER_EMAIL, email);
                w.setHeader(HEADER_USER_ID, String.valueOf(userId));
                w.setHeader(HEADER_IP_ADDRESS, clientIp);
                w.setHeader(HEADER_TRANSACTION_ID, transactionId);
                requestWrapper = w;
            } else {
                RequestWrapper rw = new RequestWrapper(request);
                rw.setHeader(HEADER_EMAIL, email);
                rw.setHeader(HEADER_USER_ID, String.valueOf(userId));
                rw.setHeader(HEADER_IP_ADDRESS, clientIp);
                rw.setHeader(HEADER_TRANSACTION_ID, transactionId);
                requestWrapper = rw;
            }

            ResponseWrapper responseWrapper = new ResponseWrapper(response);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                if (jwtPort.isTokenValid(jwt)) {
                    Collection<GrantedAuthority> authority = jwtPort.extractRoles(jwt);

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(email, null,
                                    authority);
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(requestWrapper, responseWrapper);
            response.setStatus(responseWrapper.getStatus());
            response.setContentType(request.getContentType());
            response.getOutputStream().write(responseWrapper.getByteArray());
        } catch (GenericErrorException e) {
            responseBody.setMessage(e.getMessage());
            responseBody.setTransactionId(e.transactionId);
            response.setStatus(e.httpStatus.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getOutputStream().write(new ObjectMapper().writeValueAsBytes(responseBody));
        } catch (ExpiredJwtException e) {
            responseBody.setMessage("Invalid JWT, has expired");
            responseBody.setCode(401);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getOutputStream().write(new ObjectMapper().writeValueAsBytes(responseBody));
        } catch (MalformedJwtException e) {
            responseBody.setMessage("The JWT token is invalid or malformed");
            responseBody.setCode(400);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getOutputStream().write(new ObjectMapper().writeValueAsBytes(responseBody));
        }
    }
}
