package com.register.wowlibre.domain.port.in.plan;

import com.register.wowlibre.domain.dto.PlanAdminDto;
import com.register.wowlibre.domain.dto.PlanAdminRequestDto;
import com.register.wowlibre.domain.dto.PlanDetailDto;

import java.util.List;

public interface PlanPort {

  List<PlanDetailDto> getPlan(String language, String transactionId);

  List<PlanAdminDto> getPlanAdminList(String transactionId);

  PlanAdminDto createPlanAdmin(PlanAdminRequestDto request, String transactionId);

  PlanAdminDto updatePlanAdmin(PlanAdminRequestDto request, String transactionId);

  void deletePlanAdmin(Long id, String transactionId);
}
