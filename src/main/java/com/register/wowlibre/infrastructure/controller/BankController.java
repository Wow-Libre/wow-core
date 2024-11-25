package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.port.in.bank.*;
import com.register.wowlibre.domain.shared.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/bank")
public class BankController {

    private final BankPort bankPort;

    public BankController(BankPort bankPort) {
        this.bankPort = bankPort;
    }

    @PostMapping(path = "/request")
    public ResponseEntity<GenericResponse<Void>> bank(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestBody @Valid BankRequestDto account) {

        bankPort.applyForLoan(userId, account.getAccountId(), account.getCharacterId(), account.getServerId(),
                account.getPlanId(), transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @GetMapping(path = "/available/servers")
    public ResponseEntity<GenericResponse<List<ServerAvailableBankDto>>> serverAvailableBank(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId) {

        List<ServerAvailableBankDto> servers = bankPort.serverAvailableLoan(transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(servers, transactionId).ok().build());
    }
}
