package com.register.wowlibre.domain.port.out;

import com.register.wowlibre.domain.model.resources.BenefitModel;
import com.register.wowlibre.domain.model.resources.CountryModel;
import com.register.wowlibre.domain.model.resources.PlanModel;
import com.register.wowlibre.domain.model.resources.WidgetHomeSubscriptionModel;

import java.util.List;

public interface JsonLoaderPort {

    List<CountryModel> getJsonCountry(String transactionId);

    List<PlanModel> getJsonPlans(String language, String transactionId);

    List<BenefitModel> getJsonBenefitsGuild(String language, String transactionId);

    WidgetHomeSubscriptionModel getWidgetSubscription(String language, String transactionId);

}
