package com.register.wowlibre.repository;

import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.repositories.user_validation.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JpaOtpVerificationAdapterTest {
    private UserValidationRepository userValidationRepository;
    private JpaOtpVerificationAdapter adapter;

    @BeforeEach
    void setUp() {
        userValidationRepository = mock(UserValidationRepository.class);
        adapter = new JpaOtpVerificationAdapter(userValidationRepository);
    }

    @Test
    void testSave_CallsRepositorySave() {
        OtpVerificationEntity entity = new OtpVerificationEntity();

        adapter.save(entity, "tx-001");

        verify(userValidationRepository).save(entity);
    }

    @Test
    void testFindByEmail_ReturnsEntity() {
        String email = "user@example.com";
        OtpVerificationEntity entity = new OtpVerificationEntity();
        when(userValidationRepository.findByEmail(email)).thenReturn(Optional.of(entity));

        Optional<OtpVerificationEntity> result = adapter.findByEmail(email, "tx-002");

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
        verify(userValidationRepository).findByEmail(email);
    }

    @Test
    void testFindByEmail_NotFound() {
        String email = "notfound@example.com";
        when(userValidationRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<OtpVerificationEntity> result = adapter.findByEmail(email, "tx-003");

        assertFalse(result.isPresent());
    }
}
