package com.register.wowlibre.infrastructure.controller.transaction;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.port.in.transaction.*;
import com.register.wowlibre.domain.shared.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionPort transactionPort;

    public TransactionController(TransactionPort transactionPort) {
        this.transactionPort = transactionPort;
    }

    @GetMapping
    public ResponseEntity<GenericResponse<TransactionsDto>> transactions(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestParam final Integer size,
            @RequestParam final Integer page) {

        TransactionsDto transactions = transactionPort.transactionsByUserId(userId, page, size, transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(transactions, transactionId).created().build());
    }


    @GetMapping("/{referenceNumber}")
    public ResponseEntity<GenericResponse<TransactionEntity>> getTransactionByReference(
            @PathVariable final String referenceNumber,
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId) {

        Optional<TransactionEntity> transaction = transactionPort.findByReferenceNumberAndUserId(
                referenceNumber, userId, transactionId);

        return transaction.map(transactionEntity -> ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(transactionEntity, transactionId)
                        .ok().build())).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .build());

    }
}
