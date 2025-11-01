package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.promotion.*;
import com.register.wowlibre.domain.shared.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/promotions")
public class PromotionsController {

    private final PromotionPort promotionPort;

    public PromotionsController(PromotionPort promotionPort) {
        this.promotionPort = promotionPort;
    }

    @PostMapping(path = "/create")
    public ResponseEntity<GenericResponse<Void>> create(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestBody @Valid CreatePromotionDto createPromotionDto) {

        promotionPort.create(createPromotionDto, transactionId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<Void>(transactionId).created().build());
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<PromotionModel>>> findActive(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestParam(name = PARAM_REALM_ID) final Long realmId,
            @RequestParam(required = false) final String language) {

        List<PromotionModel> promotions = promotionPort.findActiveByRealmId(realmId, language, transactionId);

        if (promotions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new GenericResponseBuilder<List<PromotionModel>>(transactionId).notContent().build());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(promotions, transactionId).ok().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<Void>> delete(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @PathVariable final Long id) {

        promotionPort.deleteLogical(id, transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }
}
