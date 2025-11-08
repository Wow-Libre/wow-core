package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.BankRequestDto;
import com.register.wowlibre.domain.dto.RealmAvailableBankDto;
import com.register.wowlibre.domain.port.in.bank.BankPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.domain.shared.GenericResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;
import static com.register.wowlibre.domain.constant.Constants.HEADER_USER_ID;

@RestController
@RequestMapping("/api/bank")
@Tag(name = "Bank", description = "APIs for managing bank loans")
public class BankController {

    private final BankPort bankPort;

    public BankController(BankPort bankPort) {
        this.bankPort = bankPort;
    }

    @Operation(summary = "Request a loan", description = "Applies for a bank loan for a game account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Loan request processed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid input or loan conditions not met"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

    @Operation(summary = "Get available loan servers", description = "Retrieves list of realms with available bank loans")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Available servers retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(path = "/available/servers")
    public ResponseEntity<GenericResponse<List<RealmAvailableBankDto>>> availableLoansByRealm(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId) {

        List<RealmAvailableBankDto> servers = bankPort.getAvailableLoansByRealm(transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(servers, transactionId).ok().build());
    }
}
