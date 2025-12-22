package com.register.wowlibre.infrastructure.controller;

import com.fasterxml.jackson.databind.*;
import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.port.in.transaction.*;
import com.register.wowlibre.domain.shared.*;
import com.register.wowlibre.infrastructure.util.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/transaction")
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

            transactionPort.purchase(request.getServerId(), request.getUserId(), request.getAccountId(),
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

            transactionPort.sendSubscriptionBenefits(request.getRealmId(), request.getUserId(), request.getAccountId(),
                    request.getCharacterId(),
                    request.getItems(), request.getBenefitType(), request.getAmount(), transactionId);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
        }
    }

    @GetMapping("/promotions")
    public ResponseEntity<GenericResponse<PromotionsDto>> promotions(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_ACCEPT_LANGUAGE) Locale locale,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestParam(name = PARAM_ACCOUNT_ID) final Long accountId,
            @RequestParam(name = PARAM_SERVER_ID) final Long serverId,
            @RequestParam(name = PARAM_CHARACTER_ID) final Long characterId,
            @RequestParam(name = "class_id") final Long classId) {

        PromotionsDto promotionsDto = transactionPort.getPromotions(serverId, userId, accountId,
                characterId, classId, locale.getLanguage(), transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(promotionsDto, transactionId).ok().build());
    }

    @PostMapping("/claim-promotions")
    public ResponseEntity<GenericResponse<Void>> claimPromotions(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_ACCEPT_LANGUAGE) Locale locale,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestBody @Valid ClaimPromoDto request) {

        transactionPort.claimPromotion(request.getServerId(), userId,
                request.getAccountId(),
                request.getCharacterId(), request.getPromotionId(), locale.getLanguage(), transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }
}
