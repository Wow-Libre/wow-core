package com.register.wowlibre.application.services.payment_method;

import com.register.wowlibre.domain.port.in.*;
import com.register.wowlibre.domain.port.out.payu_credentials.*;
import com.register.wowlibre.domain.port.out.stripe_credentials.*;
import lombok.*;
import org.springframework.stereotype.*;

@Getter
@Component
public class PaymentServiceLocator {

    private final ObtainPayuCredentials obtainPayuCredentials;
    private final ObtainStripeCredentials obtainStripeCredentials;
    private final SaveStripeCredentials saveStripeCredentials;
    private final PayuPort payuPort;
    private final SavePayuCredentials savePayUCredentials;

    public PaymentServiceLocator(ObtainPayuCredentials obtainPayuCredentials,
                                 ObtainStripeCredentials obtainStripeCredentials,
                                 SaveStripeCredentials saveStripeCredentials, PayuPort payuPort,
                                 SavePayuCredentials savePayUCredentials) {
        this.obtainPayuCredentials = obtainPayuCredentials;
        this.obtainStripeCredentials = obtainStripeCredentials;
        this.saveStripeCredentials = saveStripeCredentials;
        this.payuPort = payuPort;
        this.savePayUCredentials = savePayUCredentials;
    }


}
