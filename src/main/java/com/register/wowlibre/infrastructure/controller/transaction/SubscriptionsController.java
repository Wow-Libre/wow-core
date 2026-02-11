package com.register.wowlibre.infrastructure.controller.transaction;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.port.in.subscriptions.SubscriptionPort;
import com.register.wowlibre.domain.shared.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionsController {
    private final SubscriptionPort subscriptionPort;

    public SubscriptionsController(SubscriptionPort subscriptionPort) {
        this.subscriptionPort = subscriptionPort;
    }

    @GetMapping("/admin/list")
    public ResponseEntity<GenericResponse<SubscriptionAdminListDto>> adminList(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId) {
        SubscriptionAdminListDto response = subscriptionPort.getSubscriptionAdminList(transactionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(response, transactionId).ok().build());
    }

    @GetMapping("/pill-home")
    public ResponseEntity<GenericResponse<PillWidgetHomeDto>> pillHomeNotAuthenticated(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_ACCEPT_LANGUAGE) Locale locale) {

        PillWidgetHomeDto pill = subscriptionPort.getPillHome(null, locale.getLanguage(), transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(pill, transactionId).created().build());
    }

    @GetMapping("/pill-user")
    public ResponseEntity<GenericResponse<PillWidgetHomeDto>> pillHomeAuthenticated(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestHeader(name = HEADER_ACCEPT_LANGUAGE) Locale locale) {

        PillWidgetHomeDto pill = subscriptionPort.getPillHome(userId, locale.getLanguage(), transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(pill, transactionId).created().build());
    }

    @GetMapping
    public ResponseEntity<GenericResponse<Boolean>> activeSubscription(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId) {

        Boolean subscriptionActive = subscriptionPort.isActiveSubscription(userId, transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(subscriptionActive, transactionId).created().build());
    }

    @GetMapping("/benefits")
    public ResponseEntity<GenericResponse<SubscriptionBenefitsDto>> benefitsSubscription(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestHeader(name = HEADER_ACCEPT_LANGUAGE) Locale locale,
            @RequestParam(name = "server_id") final Long serverId) {

        SubscriptionBenefitsDto benefits = subscriptionPort.benefits(userId, serverId, locale.getLanguage(),
                transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(benefits, transactionId).created().build());
    }

    @PostMapping("/claim-benefits")
    public ResponseEntity<GenericResponse<Void>> claimBenefitsSubscription(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestHeader(name = HEADER_ACCEPT_LANGUAGE) Locale locale,
            @RequestBody @Valid ClaimSubscriptionBenefitsDto req) {

        subscriptionPort.claimBenefits(req.getServerId(), userId, req.getAccountId(), req.getCharacterId(),
                locale.getLanguage(), req.getBenefitId(), transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).created().build());
    }
}
