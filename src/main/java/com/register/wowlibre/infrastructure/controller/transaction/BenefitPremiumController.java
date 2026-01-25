package com.register.wowlibre.infrastructure.controller.transaction;

import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;

import com.register.wowlibre.domain.dto.BenefitsPremiumDto;
import com.register.wowlibre.domain.dto.CreateBenefitPremiumDto;
import com.register.wowlibre.domain.port.in.benefit_premium.BenefitPremiumPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.domain.shared.GenericResponseBuilder;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/benefit-premium")
public class BenefitPremiumController {

  private final BenefitPremiumPort benefitPremiumPort;

  public BenefitPremiumController(BenefitPremiumPort benefitPremiumPort) {
    this.benefitPremiumPort = benefitPremiumPort;
  }

  @PostMapping
  public ResponseEntity<GenericResponse<Void>> createBenefitPremium(
      @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
      @RequestBody @Valid CreateBenefitPremiumDto request) {

    benefitPremiumPort.createBenefitPremium(request, transactionId);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(new GenericResponseBuilder<Void>(transactionId).created().build());
  }

  @DeleteMapping
  public ResponseEntity<GenericResponse<Void>> delete(
      @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
      @RequestParam(name = "id") Long id) {

    benefitPremiumPort.deleteBenefitPremium(id, transactionId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(new GenericResponseBuilder<Void>(transactionId).created().build());
  }

  @GetMapping
  public ResponseEntity<GenericResponse<List<BenefitsPremiumDto>>> allBenefitsPremium(
      @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
      @RequestParam(name = "realmId") Long realmId) {

    final List<BenefitsPremiumDto> benefitsPremium =
        benefitPremiumPort.findByRealmIdStatusIsTrue(realmId, transactionId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(new GenericResponseBuilder<>(benefitsPremium, transactionId).ok().build());
  }

}
