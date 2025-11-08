package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.BankRequestDto;
import com.register.wowlibre.domain.dto.RealmAvailableBankDto;
import com.register.wowlibre.domain.port.in.bank.BankPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.domain.shared.GenericResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;
import static com.register.wowlibre.domain.constant.Constants.HEADER_USER_ID;

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
    public ResponseEntity<GenericResponse<List<RealmAvailableBankDto>>> availableLoansByRealm(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId) {

        List<RealmAvailableBankDto> servers = bankPort.getAvailableLoansByRealm(transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(servers, transactionId).ok().build());
    }
}
