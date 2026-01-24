package com.register.wowlibre.infrastructure.entities.transactions;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "stripe_credentials")
public class StripeCredentialsEntity {
    @Id
    private Long gatewayId;
    @OneToOne
    @MapsId
    @JoinColumn(name = "gateway_id")
    private PaymentGatewaysEntity gateway;
    @Column(name = "api_secret", nullable = false)
    private String apiSecret;
    @Column(name = "api_public", nullable = false)
    private String apiPublic;
    @Column(name = "success_url", nullable = false)
    private String successUrl;
    @Column(name = "cancel_url", nullable = false)
    private String cancelUrl;
    @Column(name = "webhook_url", nullable = false)
    private String webhookUrl;
    @Column(name = "webhook_secret", nullable = false)
    private String webHookSecret;
}
