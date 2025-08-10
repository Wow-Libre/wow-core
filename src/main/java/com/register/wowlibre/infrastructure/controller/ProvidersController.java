package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.notification_provider.*;
import com.register.wowlibre.domain.shared.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/provider")
public class ProvidersController {

    private final NotificationProviderPort notificationProviderPort;

    public ProvidersController(NotificationProviderPort notificationProviderPort) {
        this.notificationProviderPort = notificationProviderPort;
    }

    @PostMapping
    public ResponseEntity<GenericResponse<Void>> save(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestBody @Valid CreateNotificationProviderDto request) {
        notificationProviderPort.configProvider(request, transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @DeleteMapping
    public ResponseEntity<GenericResponse<Void>> delete(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestParam(name = PARAM_PROVIDER_ID) final Long providerId) {
        notificationProviderPort.deleteProvider(providerId, transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<NotificationProviderModel>>> all(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId) {
        List<NotificationProviderModel> providers = notificationProviderPort.allProviders(transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(providers, transactionId).ok().build());
    }
}
