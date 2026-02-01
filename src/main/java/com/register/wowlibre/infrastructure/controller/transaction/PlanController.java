package com.register.wowlibre.infrastructure.controller.transaction;

import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;

import com.register.wowlibre.domain.dto.PlanDetailDto;
import com.register.wowlibre.domain.port.in.plan.PlanPort;
import com.register.wowlibre.domain.shared.GenericResponse;
import com.register.wowlibre.domain.shared.GenericResponseBuilder;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plan")
public class PlanController {
  private final PlanPort planPort;

  public PlanController(PlanPort planPort) {
    this.planPort = planPort;
  }

  @GetMapping
  public ResponseEntity<GenericResponse<List<PlanDetailDto>>> planAvailable(
      @RequestParam(name = "language") String language,
      @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId) {

    final List<PlanDetailDto> plans = planPort.getPlan(language, transactionId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(new GenericResponseBuilder<>(plans, transactionId).ok().build());
  }

}
