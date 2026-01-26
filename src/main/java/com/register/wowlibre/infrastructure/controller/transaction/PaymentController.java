package com.register.wowlibre.infrastructure.controller.transaction;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.port.in.payment.*;
import com.register.wowlibre.domain.shared.*;
import jakarta.validation.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.register.wowlibre.domain.constant.Constants.*;
import static java.lang.Integer.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(PaymentController.class);
    private final PaymentPort paymentPort;

    public PaymentController(PaymentPort paymentPort) {
        this.paymentPort = paymentPort;
    }

    @PostMapping(value = "/notification", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<GenericResponse<Void>> notification(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestParam Map<String, String> params) {

        final String referenceSale = params.get("reference_sale");
        final String valueRaw = params.get("value");
        final String statePol = params.get("state_pol");

        // === Firma OK: procesar pago ===
        paymentPort.processPayment(PaymentTransaction.builder()
                .date(params.get("date"))
                .sign(params.get("sign"))
                .statePol(statePol)
                .paymentMethodType(parseInt(params.get("payment_method_type")))
                .paymentMethod(params.get("payment_method"))
                .currency(params.get("currency"))
                .merchantId(params.get("merchant_id"))
                .value(valueRaw)
                .responseMessagePol(params.get("response_message_pol"))
                .paymentMethodName(params.get("payment_method_name"))
                .emailBuyer(params.get("email_buyer"))
                .accountId(params.get("account_id"))
                .referenceSale(referenceSale)
                .sign(params.get("sign"))
                .build(), PaymentType.PAYU, transactionId);

        LOGGER.info("✅ Notificación procesada correctamente. referenceSale=[{}], value=[{}], statePol=[{}]",
                referenceSale, valueRaw, statePol);

        return ResponseEntity.ok(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @PostMapping("/notification/stripe")
    public ResponseEntity<GenericResponse<Void>> handleStripeEvent(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> rawMap = mapper.readValue(payload, new TypeReference<>() {
        });

        Map<String, Object> data = (Map<String, Object>) rawMap.get("data");
        if (data == null) {
            LOGGER.warn("⚠️ Payload sin campo 'data'. No es un evento válido de Stripe.");
            throw new InternalException("", transactionId);
        }

        Map<String, Object> charge = (Map<String, Object>) data.get("object");
        if (charge == null) {
            LOGGER.warn("⚠️ 'data.object' vacío. No se encontró el objeto de pago.");
            throw new InternalException("", transactionId);
        }

        Map<String, Object> metadata = (Map<String, Object>) charge.get("metadata");
        String referenceSale = metadata != null ? (String) metadata.get("referenceCode") : null;

        paymentPort.processPayment(PaymentTransaction.builder()
                .stripePayment(new PaymentTransaction.StripePayment(payload, (String) charge.get("status"),
                        (Boolean) charge.get("paid"), (Boolean) charge.get("paid")))
                .currency((String) charge.get("currency"))
                .referenceSale(referenceSale)
                .sign(sigHeader)
                .build(), PaymentType.STRIPE, transactionId);

        return ResponseEntity.ok(new GenericResponseBuilder<Void>(transactionId).ok().build());
    }

    @PostMapping
    public ResponseEntity<GenericResponse<CreatePaymentRedirectDto>> createPayment(
            @RequestHeader(name = HEADER_TRANSACTION_ID, required = false) final String transactionId,
            @RequestHeader(name = HEADER_USER_ID) final Long userId,
            @RequestHeader(name = HEADER_EMAIL) final String email,
            @RequestBody @Valid CreatePaymentDto createPaymentDto) {

        CreatePaymentRedirectDto payment = paymentPort.createPayment(userId, email, createPaymentDto,
                transactionId);

        return ResponseEntity.status(HttpStatus.OK).body(new GenericResponseBuilder<>(payment, transactionId).created().build());
    }
}
