package com.register.wowlibre.infrastructure.controller.transaction;

import static com.register.wowlibre.domain.constant.Constants.HEADER_TRANSACTION_ID;

import com.register.wowlibre.domain.dto.PlanAdminDto;
import com.register.wowlibre.domain.dto.PlanAdminRequestDto;
import com.register.wowlibre.domain.port.in.plan.PlanPort;
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

@RestController
@RequestMapping("/api/plan/admin")
public class PlanAdminController {

  private final PlanPort planPort;

  public PlanAdminController(PlanPort planPort) {
    this.planPort = planPort;
  }

  @GetMapping("/list")
  public ResponseEntity<GenericResponse<List<PlanAdminDto>>> list(
      @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId) {
    List<PlanAdminDto> list = planPort.getPlanAdminList(transactionId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new GenericResponseBuilder<>(list, transactionId).ok().build());
  }

  @PostMapping
  public ResponseEntity<GenericResponse<PlanAdminDto>> create(
      @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
      @RequestBody @Valid PlanAdminRequestDto request) {
    PlanAdminDto created = planPort.createPlanAdmin(request, transactionId);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new GenericResponseBuilder<>(created, transactionId).created().build());
  }

  @PutMapping
  public ResponseEntity<GenericResponse<PlanAdminDto>> update(
      @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
      @RequestBody @Valid PlanAdminRequestDto request) {
    PlanAdminDto updated = planPort.updatePlanAdmin(request, transactionId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new GenericResponseBuilder<>(updated, transactionId).ok().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<GenericResponse<Void>> delete(
      @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) String transactionId,
      @PathVariable Long id) {
    planPort.deletePlanAdmin(id, transactionId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
  }
}
