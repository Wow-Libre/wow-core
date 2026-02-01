package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.guild_benefits_catalog.*;
import com.register.wowlibre.domain.port.in.guild_benefits_catalog.*;
import com.register.wowlibre.domain.shared.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/benefits-guild")
public class BenefitsGuildController {

    private final GuildBenefitsCatalogPort guildBenefitsCatalogPort;

    public BenefitsGuildController(GuildBenefitsCatalogPort guildBenefitsCatalogPort) {
        this.guildBenefitsCatalogPort = guildBenefitsCatalogPort;
    }

    @GetMapping("/all")
    public ResponseEntity<GenericResponse<List<GuildBenefitsCatalogDto>>> benefitsGuilds(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_ACCEPT_LANGUAGE) Locale locale) {
        final List<GuildBenefitsCatalogDto> benefitsGuild =
                guildBenefitsCatalogPort.getAllBenefits(locale.getLanguage(), transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(benefitsGuild, transactionId).ok().build());
    }

    @PostMapping
    public ResponseEntity<GenericResponse<Void>> create(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_ACCEPT_LANGUAGE) Locale locale,
            @RequestBody @Valid CreateGuildBenefitsCatalog createGuildBenefitsCatalog) {
        guildBenefitsCatalogPort.create(createGuildBenefitsCatalog, transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @PutMapping
    public ResponseEntity<GenericResponse<Void>> update(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_ACCEPT_LANGUAGE) Locale locale,
            @RequestBody @Valid UpdateGuildBenefitsCatalog updateGuildBenefitsCatalog) {
        guildBenefitsCatalogPort.update(updateGuildBenefitsCatalog, transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @DeleteMapping
    public ResponseEntity<GenericResponse<Void>> delete(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_ACCEPT_LANGUAGE) Locale locale,
            @RequestBody @Valid UpdateGuildBenefitsCatalog updateGuildBenefitsCatalog) {
        guildBenefitsCatalogPort.update(updateGuildBenefitsCatalog, transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

}
