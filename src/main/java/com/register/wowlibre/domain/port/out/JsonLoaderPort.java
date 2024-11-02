package com.register.wowlibre.domain.port.out;

import com.register.wowlibre.domain.model.*;

import java.util.*;

public interface JsonLoaderPort {
    List<CountryModel> getJsonCountry(String transactionId);

    List<FaqsModel> getJsonFaqs(String language, String transactionId);

    List<PlanModel> getJsonPlans(String language, String transactionId);

    List<BenefitModel> getJsonBenefitsGuild(String language, String transactionId);

    List<ServersPromotions> getJsonServersPromoGuild(String language, String transactionId);

}
