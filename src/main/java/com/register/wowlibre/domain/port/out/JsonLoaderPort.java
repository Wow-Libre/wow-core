package com.register.wowlibre.domain.port.out;

import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.model.resources.*;

import java.util.*;

public interface JsonLoaderPort {

    List<CountryModel> getJsonCountry(String transactionId);

    List<PlanModel> getJsonPlans(String language, String transactionId);


    WidgetHomeSubscriptionModel getWidgetSubscription(String language, String transactionId);

    PillHomeModel getResourcePillHome(String language, String transactionId);

}
