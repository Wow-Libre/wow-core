package com.register.wowlibre.infrastructure.controller.transaction;

import com.fasterxml.jackson.databind.*;
import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.port.in.transaction.*;
import com.register.wowlibre.domain.shared.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import com.register.wowlibre.infrastructure.util.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionPort transactionPort;
    private final SignatureService signatureService;
    private final ObjectMapper objectMapper;

    public TransactionController(TransactionPort transactionPort, SignatureService signatureService,
                                 ObjectMapper objectMapper) {
        this.transactionPort = transactionPort;
        this.signatureService = signatureService;
        this.objectMapper = objectMapper;
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

    @PostMapping("/purchase")
    public ResponseEntity<GenericResponse<Void>> sendItems(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_SIGNATURE) final String signature,
            @RequestBody @Valid CreateTransactionItemsDto request) {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(request);

            if (!signatureService.validateSignature(requestBodyJson, signature)) {
                return ResponseEntity.internalServerError()
                        .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
            }

            transactionPort.purchase(request.getRealmId(), request.getUserId(), request.getAccountId(),
                    request.getReference(),
                    request.getItems(), request.getAmount(), transactionId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @PostMapping("/subscription-benefits")
    public ResponseEntity<GenericResponse<Void>> sendSubscriptionBenefits(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_SIGNATURE) final String signature,
            @RequestBody @Valid SubscriptionBenefitsDto request) {

        try {
            String requestBodyJson = objectMapper.writeValueAsString(request);

            if (!signatureService.validateSignature(requestBodyJson, signature)) {
                return ResponseEntity.internalServerError()
                        .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
            }


            return ResponseEntity.status(HttpStatus.OK)
                    .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
        }
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
