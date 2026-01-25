package com.register.wowlibre.application.services.benefit_premium;

import com.register.wowlibre.domain.dto.BenefitPremiumItemDto;
import com.register.wowlibre.domain.dto.BenefitsPremiumDto;
import com.register.wowlibre.domain.dto.CreateBenefitPremiumDto;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.port.in.benefit_premium.BenefitPremiumPort;
import com.register.wowlibre.domain.port.out.benefit_premiums.ObtainBenefitPremium;
import com.register.wowlibre.domain.port.out.benefit_premiums.SaveBenefitPremium;
import com.register.wowlibre.infrastructure.entities.transactions.BenefitPremiumEntity;
import com.register.wowlibre.infrastructure.entities.transactions.BenefitPremiumItemEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class BenefitPremiumService implements BenefitPremiumPort {

  private final ObtainBenefitPremium obtainBenefitPremium;
  private final SaveBenefitPremium saveBenefitPremium;

  public BenefitPremiumService(ObtainBenefitPremium obtainBenefitPremium, SaveBenefitPremium saveBenefitPremium) {
    this.obtainBenefitPremium = obtainBenefitPremium;
    this.saveBenefitPremium = saveBenefitPremium;
  }

  @Override
  public void createBenefitPremium(CreateBenefitPremiumDto request, String transactionId) {
    BenefitPremiumEntity benefitPremiumEntity = new BenefitPremiumEntity();
    benefitPremiumEntity.setImg(request.getImg());
    benefitPremiumEntity.setName(request.getName());
    benefitPremiumEntity.setDescription(request.getDescription());
    benefitPremiumEntity.setCommand(request.getCommand());
    benefitPremiumEntity.setSendItem(request.isSendItem());
    benefitPremiumEntity.setReactivable(request.isReactivable());
    benefitPremiumEntity.setBtnText(request.getBtnText());
    benefitPremiumEntity.setType(request.getType());
    benefitPremiumEntity.setRealmId(request.getRealmId());
    benefitPremiumEntity.setLanguage(request.getLanguage());
    benefitPremiumEntity.setAmount(request.getAmount() != null ? request.getAmount() : 0);
    benefitPremiumEntity.setStatus(true);

    // Guardar items si existen
    if (request.getItems() != null && !request.getItems().isEmpty()) {
      List<BenefitPremiumItemEntity> items = new ArrayList<>();
      for (BenefitPremiumItemDto itemDto : request.getItems()) {
        BenefitPremiumItemEntity itemEntity = new BenefitPremiumItemEntity();
        itemEntity.setCode(itemDto.getCode());
        itemEntity.setQuantity(itemDto.getQuantity());
        itemEntity.setBenefitPremium(benefitPremiumEntity);
        items.add(itemEntity);
      }
      benefitPremiumEntity.setItems(items);
    }

    saveBenefitPremium.save(benefitPremiumEntity);
  }

  @Override
  public void deleteBenefitPremium(Long id, String transactionId) {

    Optional<BenefitPremiumEntity> benefitPremium = obtainBenefitPremium.findById(id);

    if (benefitPremium.isEmpty()) {
      throw new InternalException("Benefit Not found", transactionId);
    }

    BenefitPremiumEntity benefitPremiumEntity = benefitPremium.get();
    benefitPremiumEntity.setStatus(false);
    saveBenefitPremium.save(benefitPremiumEntity);
  }


  @Override
  public List<BenefitsPremiumDto> findByLanguageAndRealmId(String language, Long realmId, String transactionId) {
    return obtainBenefitPremium.findByRealmIdAndLanguageAndStatusIsTrue(realmId, language, transactionId).stream().map(this::mapToModel)
        .toList();
  }

  @Override
  public List<BenefitsPremiumDto> findByRealmIdStatusIsTrue(Long realmId, String transactionId) {
    return obtainBenefitPremium.findByRealmIdAndStatusIsTrue(realmId).stream().map(this::mapToModel)
        .toList();
  }

  private BenefitsPremiumDto mapToModel(BenefitPremiumEntity benefitPremiumEntity) {
    List<BenefitPremiumItemDto> items = new ArrayList<>();
    if (benefitPremiumEntity.getItems() != null) {
      items = benefitPremiumEntity.getItems().stream()
          .map(item -> BenefitPremiumItemDto.builder()
              .code(item.getCode())
              .quantity(item.getQuantity())
              .build())
          .toList();
    }

    return BenefitsPremiumDto.builder()
        .id(benefitPremiumEntity.getId())
        .name(benefitPremiumEntity.getName())
        .description(benefitPremiumEntity.getDescription())
        .img(benefitPremiumEntity.getImg())
        .command(benefitPremiumEntity.getCommand())
        .amount(benefitPremiumEntity.getAmount())
        .sendItem(benefitPremiumEntity.isSendItem())
        .reactivable(benefitPremiumEntity.isReactivable())
        .btnText(benefitPremiumEntity.getBtnText())
        .type(benefitPremiumEntity.getType())
        .realmId(benefitPremiumEntity.getRealmId())
        .language(benefitPremiumEntity.getLanguage())
        .status(benefitPremiumEntity.isStatus())
        .items(items)
        .build();
  }
}
