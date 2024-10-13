package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.*;
import com.register.wowlibre.domain.shared.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/resources")
public class ResourcesController {

    private final ResourcesPort resourcesPort;

    public ResourcesController(ResourcesPort resourcesPort) {
        this.resourcesPort = resourcesPort;
    }

    @GetMapping("/country")
    public ResponseEntity<GenericResponse<List<CountryModel>>> country(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId) {
        final List<CountryModel> countryModelList = resourcesPort.getCountry(transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(countryModelList, transactionId).ok().build());
    }

    @GetMapping("/faqs")
    public ResponseEntity<GenericResponse<List<FaqsModel>>> faqs(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId) {
        final List<FaqsModel> countryModelList = resourcesPort.getFaqs(transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(countryModelList, transactionId).ok().build());
    }

    @GetMapping("/bank/plans")
    public ResponseEntity<GenericResponse<List<PlanModel>>> plans(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestParam(name = "language", defaultValue = "es") final String language) {

        final List<PlanModel> countryModelList = resourcesPort.getPlansBank(language, transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(countryModelList, transactionId).ok().build());
    }

    @GetMapping("/benefits-guild")
    public ResponseEntity<GenericResponse<List<BenefitModel>>> benefitsGuild(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestParam(name = "language", defaultValue = "es") final String language) {
        final List<BenefitModel> benefitsGuild = resourcesPort.getBenefitsGuild(language, transactionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(benefitsGuild, transactionId).ok().build());
    }
}
