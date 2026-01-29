package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.model.resources.*;
import com.register.wowlibre.domain.port.in.guild_benefits_catalog.*;
import com.register.wowlibre.domain.shared.*;
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

    @GetMapping
    public ResponseEntity<GenericResponse<List<BenefitModel>>> benefitsGuild(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_ACCEPT_LANGUAGE) Locale locale) {
        final List<BenefitModel> benefitsGuild = guildBenefitsCatalogPort.getAllBenefits(locale.getLanguage(),
                transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(benefitsGuild, transactionId).ok().build());
    }
}
