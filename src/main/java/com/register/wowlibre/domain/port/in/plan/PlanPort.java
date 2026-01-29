package com.register.wowlibre.domain.port.in.plan;

import com.register.wowlibre.domain.dto.*;

import java.util.*;

public interface PlanPort {
    List<PlanDetailDto> getPlan(String language, String transactionId);
}
