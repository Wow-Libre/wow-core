package com.register.wowlibre.infrastructure.controller;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.port.in.banners.*;
import com.register.wowlibre.domain.shared.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/banners")
@Tag(name = "Banners", description = "APIs for managing banners")
public class BannersController {

    private final BannersPort bannersPort;

    public BannersController(BannersPort bannersPort) {
        this.bannersPort = bannersPort;
    }

    @Operation(summary = "Get banners by language", description = "Retrieves list of banners filtered by language")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Banners retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<GenericResponse<List<BannerModel>>> banners(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_ACCEPT_LANGUAGE) Locale locale) {

        List<BannerModel> banners = bannersPort.findByLanguage(locale.getLanguage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(banners, transactionId).ok().build());
    }

    @Operation(summary = "Create a banner", description = "Creates a new banner (image or video). Maximum 5 banners per language and type")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Banner created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid banner type or validation failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<GenericResponse<Void>> saveBanner(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestBody @Valid BannerDto bannerDto) {
        bannersPort.saveBanner(bannerDto, transactionId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponseBuilder<Void>(transactionId).created().build());
    }

    @Operation(summary = "Delete a banner", description = "Deletes a banner by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Banner deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{bannerId}")
    public ResponseEntity<GenericResponse<Void>> deleteBanner(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @PathVariable Long bannerId) {
        bannersPort.deleteBanner(bannerId, transactionId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }
}
