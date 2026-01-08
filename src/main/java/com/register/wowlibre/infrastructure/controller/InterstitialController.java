package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.interstitial.*;
import com.register.wowlibre.domain.port.in.interstitial.*;
import com.register.wowlibre.domain.shared.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/interstitial")
public class InterstitialController {
    private final InterstitialPort interstitialPort;

    public InterstitialController(InterstitialPort interstitialPort) {
        this.interstitialPort = interstitialPort;
    }

    @GetMapping
    public ResponseEntity<GenericResponse<InterstitialDto>> interstitial(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestParam(name = PARAM_ACCOUNT_ID) final Long accountId,
            @RequestParam(name = PARAM_SERVER_ID) final Long realmId) {

        InterstitialDto response = interstitialPort.findAllActiveInterstitial(userId, transactionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(response, transactionId).ok().build());
    }

    @PostMapping
    public ResponseEntity<GenericResponse<Void>> create(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestBody final CreateInterstitial request) {

        interstitialPort.createInterstitial(request.getUrlImg(), request.getRedirectUrl(),
                transactionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<GenericResponse<Void>> delete(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @PathVariable(name = "id") final Long interstitialId) {
        interstitialPort.deleteInterstitial(interstitialId, transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }
}
