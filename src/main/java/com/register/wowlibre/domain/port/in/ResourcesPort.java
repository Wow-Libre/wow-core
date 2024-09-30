package com.register.wowlibre.domain.port.in;


import com.register.wowlibre.domain.model.*;

import java.util.*;

public interface ResourcesPort {
    List<CountryModel> getCountry(String transactionId);

    List<FaqsModel> getFaqs(String transactionId);

    List<PlanModel> getPlansBank(String language, String transactionId);

}
