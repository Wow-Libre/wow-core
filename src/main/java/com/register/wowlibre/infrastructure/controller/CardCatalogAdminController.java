package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.user_card.CardCatalogAdminDto;
import com.register.wowlibre.domain.dto.user_card.CardCatalogAdminRequestDto;
import com.register.wowlibre.domain.port.in.user_card.CardCatalogAdminPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.domain.shared.GenericResponseBuilder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;

@RestController
@RequestMapping("/api/cards/admin/catalog")
public class CardCatalogAdminController {

    private final CardCatalogAdminPort cardCatalogAdminPort;

    public CardCatalogAdminController(CardCatalogAdminPort cardCatalogAdminPort) {
        this.cardCatalogAdminPort = cardCatalogAdminPort;
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<CardCatalogAdminDto>>> list(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId) {
        List<CardCatalogAdminDto> list = cardCatalogAdminPort.listAll(transactionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(list, transactionId).ok().build());
    }

    @PostMapping
    public ResponseEntity<GenericResponse<CardCatalogAdminDto>> create(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @RequestBody @Valid CardCatalogAdminRequestDto request) {
        CardCatalogAdminDto created = cardCatalogAdminPort.create(request, transactionId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<>(created, transactionId).created().build());
    }

    @PutMapping("/{code}")
    public ResponseEntity<GenericResponse<CardCatalogAdminDto>> update(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @PathVariable String code,
            @RequestBody @Valid CardCatalogAdminRequestDto request) {
        CardCatalogAdminDto updated = cardCatalogAdminPort.update(code, request, transactionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(updated, transactionId).ok().build());
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<GenericResponse<Void>> delete(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
            @PathVariable String code) {
        cardCatalogAdminPort.delete(code, transactionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }
}
