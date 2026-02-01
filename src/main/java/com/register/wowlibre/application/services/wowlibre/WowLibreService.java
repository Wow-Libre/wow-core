package com.register.wowlibre.application.services.wowlibre;

import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.model.ItemQuantityModel;
import com.register.wowlibre.domain.port.in.integrator.IntegratorPort;
import com.register.wowlibre.domain.port.in.realm.RealmPort;
import com.register.wowlibre.domain.port.in.wowlibre.WowLibrePort;
import com.register.wowlibre.infrastructure.entities.RealmEntity;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WowLibreService implements WowLibrePort {
  private final IntegratorPort integratorPort;
  private final RealmPort realmPort;

  public WowLibreService(IntegratorPort integratorPort, RealmPort realmPort) {
    this.integratorPort = integratorPort;
    this.realmPort = realmPort;
  }

  @Override
  public void sendPurchases(Long realmId, Long userId, Long accountId, Double gold,
                            List<ItemQuantityModel> items, String reference, String transactionId) {
    RealmEntity realm = realmPort.findById(realmId, transactionId)
        .orElseThrow(() -> new InternalException("Realm not found", transactionId));
    
    integratorPort.purchase(realm.getHost(), realm.getJwt(), userId, accountId, reference, items, gold, transactionId);
  }

  @Override
  public void sendBenefitsPremium(Long realmId, Long userId, Long accountId,
                                  Long characterId,
                                  List<ItemQuantityModel> items, String benefitType, Double amount,
                                  String transactionId) {
    RealmEntity realm = realmPort.findById(realmId, transactionId)
        .orElseThrow(() -> new InternalException("Realm not found", transactionId));
    
    integratorPort.sendBenefitsPremium(realm.getHost(), realm.getJwt(), userId, accountId,
        characterId, items, benefitType, amount, transactionId);
  }
}
