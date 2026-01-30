package com.register.wowlibre.controller;

import com.fasterxml.jackson.databind.*;
import com.register.wowlibre.domain.port.in.transaction.*;
import com.register.wowlibre.infrastructure.controller.transaction.*;
import com.register.wowlibre.infrastructure.util.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionPort transactionPort;

    @Mock
    private SignatureService signatureService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private TransactionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

}

