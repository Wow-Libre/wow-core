package com.register.wowlibre.infrastructure.controller.transaction;

import com.register.wowlibre.domain.port.in.wallet.*;
import com.register.wowlibre.domain.shared.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    private final WalletPort walletPort;

    public WalletController(WalletPort walletPort) {
        this.walletPort = walletPort;
    }

    @GetMapping
    public ResponseEntity<GenericResponse<Long>> points(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId) {

        final Long walletPoints = walletPort.getPoints(userId, transactionId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(walletPoints, transactionId).ok().build());
    }

}
