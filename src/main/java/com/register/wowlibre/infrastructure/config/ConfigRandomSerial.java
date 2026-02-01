package com.register.wowlibre.infrastructure.config;

import com.register.wowlibre.infrastructure.util.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

@Component
public class ConfigRandomSerial {
    @Bean("resetPasswordString")
    public RandomString configRandomString() {
        return new RandomString(15, "abcdefghijklmnopqrstuvwxyz0123456789");
    }

    @Bean("referenceSerialBank")
    public RandomString referenceSerial() {
        return new RandomString(20, "abcdefghijklmnopqrstuvwxyz0123456789");
    }

    @Bean("randomCode")
    public RandomString randomCodeString() {
        return new RandomString(28, "abcdefghijklmnopqrstuvwxyz0123456789");
    }

    @Bean("randomSendOtp")
    public RandomString configRandomOtpString() {
        return new RandomString(5, "abcdefghijklmnopqrstuvwxyz0123456789");
    }


    @Bean("productReference")
    public RandomString productRandomString() {
        return new RandomString(30, "abcdefghijklmnopqrstuvwxyz0123456789");
    }

    @Bean("subscriptionReference")
    public RandomString subscriptionRandomString() {
        return new RandomString(40, "abcdefghijklmnopqrstuvwxyz0123456789");
    }


}
