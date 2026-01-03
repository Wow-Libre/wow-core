package com.register.wowlibre.client;

import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.infrastructure.client.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.http.*;
import org.springframework.web.client.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleClientTest {
    private final String transactionId = "tx-123";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GoogleClient googleClient; // ✅ instancia REAL

    private VerifyCaptchaRequest request;

    @BeforeEach
    void setUp() {
        request = new VerifyCaptchaRequest(
                "secret-key",
                "captcha-response",
                "127.0.0.1"
        );
    }

    @Test
    void verifyRecaptcha_successfulResponse_returnsBody() {
        VerifyCaptchaResponse expectedResponse = new VerifyCaptchaResponse();
        expectedResponse.setSuccess(true);

        ResponseEntity<VerifyCaptchaResponse> responseEntity =
                new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(VerifyCaptchaResponse.class)
        )).thenReturn(responseEntity);

        VerifyCaptchaResponse result =
                googleClient.verifyRecaptcha(request, transactionId);

        assertNotNull(result);
        assertTrue(result.getSuccess());
    }

    @Test
    void verifyRecaptcha_2xxWithNullBody_throwsInternalException() {
        ResponseEntity<VerifyCaptchaResponse> responseEntity =
                new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(VerifyCaptchaResponse.class)
        )).thenReturn(responseEntity);

        assertThrows(
                InternalException.class,
                () -> googleClient.verifyRecaptcha(request, transactionId)
        );
    }

    @Test
    void verifyRecaptcha_clientError_throwsInternalException() {
        HttpClientErrorException exception =
                HttpClientErrorException.create(
                        HttpStatus.BAD_REQUEST,
                        "Bad Request",
                        HttpHeaders.EMPTY,
                        "error".getBytes(),
                        null
                );

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(VerifyCaptchaResponse.class)
        )).thenThrow(exception);

        assertThrows(
                InternalException.class,
                () -> googleClient.verifyRecaptcha(request, transactionId)
        );
    }

    @Test
    void verifyRecaptcha_serverError_throwsInternalException() {
        HttpServerErrorException exception =
                HttpServerErrorException.create(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Server Error",
                        HttpHeaders.EMPTY,
                        "error".getBytes(),
                        null
                );

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(VerifyCaptchaResponse.class)
        )).thenThrow(exception);

        assertThrows(
                InternalException.class,
                () -> googleClient.verifyRecaptcha(request, transactionId)
        );
    }

    @Test
    void verifyRecaptcha_unexpectedException_throwsInternalException() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(VerifyCaptchaResponse.class)
        )).thenThrow(new RuntimeException("Boom"));

        InternalException ex = assertThrows(
                InternalException.class,
                () -> googleClient.verifyRecaptcha(request, transactionId)
        );

        assertTrue(ex.getMessage().contains("Unexpected"));
    }
}
