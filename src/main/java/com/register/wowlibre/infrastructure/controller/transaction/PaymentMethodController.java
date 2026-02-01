package com.register.wowlibre.infrastructure.controller.transaction;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.port.in.payment_gateways.*;
import com.register.wowlibre.domain.shared.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;

@RestController
@RequestMapping("/api/payment-method")
public class PaymentMethodController {

    private final PaymentGatewaysPort gatewayService;

    public PaymentMethodController(PaymentGatewaysPort gatewayService) {
        this.gatewayService = gatewayService;
    }

    @PostMapping
    public ResponseEntity<GenericResponse<Void>> createPaymentMethod(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestBody @Valid CreatePaymentMethodDto createPaymentMethodDto) {

        gatewayService.createPayment(createPaymentMethodDto.getPaymentType(), createPaymentMethodDto.getName(),
                createPaymentMethodDto.getCredentials(), transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @DeleteMapping("/{paymentTypeId}")
    public ResponseEntity<GenericResponse<Void>> deletePaymentMethod(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @PathVariable Long paymentTypeId) {

        gatewayService.deletePayment(paymentTypeId, transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @GetMapping
    public ResponseEntity<GenericResponse<List<PaymentMethodsDto>>> paymentMethods(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId) {

        List<PaymentMethodsDto> paymentMethods = gatewayService.paymentMethods(transactionId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponseBuilder<>(paymentMethods, transactionId).ok().build());
    }
}
