package com.register.wowlibre.domain.dto.client.payu;

import lombok.*;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayUOrderDetailResponse {
    private String code;
    private String error;
    private PayUResult result;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayUResult {
        private List<PayUOrder> payload;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayUOrder {
        private Long id;
        private Long accountId;
        private String status;
        private String referenceCode;
        private String description;
        private String airlineCode;
        private String language;
        private String notifyUrl;
        private PayUShippingAddress shippingAddress;
        private PayUBuyer buyer;
        private String antifraudMerchantId;
        private Boolean isTest;
        private List<PayUTransaction> transactions;
        private Map<String, PayUAdditionalValue> additionalValues;
        private Long creationDate;
        private Boolean isCreatedUsingStandardIntegrationParams;
        private Long merchantId;
        private String processedTransactionId;
        private String orderSignature;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayUShippingAddress {
        private String street1;
        private String street2;
        private String city;
        private String state;
        private String country;
        private String postalCode;
        private String phone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayUBuyer {
        private String merchantBuyerId;
        private String fullName;
        private String emailAddress;
        private String contactPhone;
        private PayUBuyerAddress buyerAddress;
        private String dniNumber;
        private String cnpj;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayUBuyerAddress {
        private String street1;
        private String street2;
        private String city;
        private String state;
        private String country;
        private String postalCode;
        private String phone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayUTransaction {
        private String id;
        private String order;
        private PayUCreditCard creditCard;
        private String bankAccount;
        private String type;
        private String parentTransactionId;
        private String paymentMethod;
        private String source;
        private String paymentCountry;
        private PayUTransactionResponse transactionResponse;
        private String deviceSessionId;
        private String ipAddress;
        private String cookie;
        private String userAgent;
        private String expirationDate;
        private PayUPayer payer;
        private Long termsAndConditionId;
        private Map<String, PayUAdditionalValue> additionalValues;
        private Map<String, String> extraParameters;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayUCreditCard {
        private String maskedNumber;
        private String issuerBank;
        private String name;
        private String cardType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayUTransactionResponse {
        private String state;
        private String paymentNetworkResponseCode;
        private String paymentNetworkResponseErrorMessage;
        private String trazabilityCode;
        private String authorizationCode;
        private String pendingReason;
        private String responseCode;
        private String errorCode;
        private String responseMessage;
        private String transactionDate;
        private String transactionTime;
        private Long operationDate;
        private String extraParameters;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayUPayer {
        private String merchantPayerId;
        private String fullName;
        private PayUPayerAddress billingAddress;
        private String emailAddress;
        private String contactPhone;
        private String dniNumber;
        private String dniType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayUPayerAddress {
        private String street1;
        private String street2;
        private String city;
        private String state;
        private String country;
        private String postalCode;
        private String phone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayUAdditionalValue {
        private Double value;
        private String currency;
    }
}
