package com.register.wowlibre.infrastructure.entities.transactions;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "payu_credentials")
public class PayuCredentialsEntity {
    @Id
    private Long gatewayId;
    @OneToOne
    @MapsId
    @JoinColumn(name = "gateway_id")
    private PaymentGatewaysEntity gateway;
    @Column(name = "host", nullable = false)
    private String host;
    @Column(name = "api_key", nullable = false)
    private String apiKey;
    @Column(name = "api_login", nullable = false)
    private String apiLogin;
    @Column(name = "key_public", nullable = false)
    private String keyPublic;
    @Column(name = "success_url", nullable = false)
    private String successUrl;
    @Column(name = "cancel_url")
    private String cancelUrl;
    @Column(name = "webhook_url", nullable = false)
    private String webhookUrl;
    @Column(name = "merchant_id", nullable = false)
    private String merchantId;
    @Column(name = "account_id", nullable = false)
    private String accountId;

}
