package com.register.wowlibre.infrastructure.schedule;

import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.port.in.auth_integrator.*;
import com.register.wowlibre.domain.port.out.realm.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.slf4j.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
public class RealmsSchedule {
    private static final Logger LOGGER = LoggerFactory.getLogger(RealmsSchedule.class);

    private final ObtainRealmPort obtainRealmPort;
    private final SaveRealmPort saveRealmPort;
    private final AuthIntegratorPort authIntegratorPort;

    public RealmsSchedule(ObtainRealmPort obtainRealmPort, AuthIntegratorPort authIntegratorPort,
                          SaveRealmPort saveRealmPort) {
        this.obtainRealmPort = obtainRealmPort;
        this.authIntegratorPort = authIntegratorPort;
        this.saveRealmPort = saveRealmPort;
    }

    @Scheduled(cron = "1 0/15 * * * *")
    public void LoginRealm() {
        final String transactionId = UUID.randomUUID().toString();
        List<RealmEntity> realms = obtainRealmPort.findByStatusIsTrue(transactionId);

        for (RealmEntity realm : realms) {
            LOGGER.info("[RealmsSchedule][LoginRealm] Automatic start of JWT refresh  {} ", realm.getName());

            try {
                Date expirationDate = realm.getExpirationDate();

                if (expirationDate == null || isExpirationDateExpired(expirationDate)) {
                    Date adjustedExpirationDate = expirationDate != null ? expirationDate :
                            new Date();

                    if (adjustedExpirationDate.before(new Date())) {

                        AuthClientResponse authToken = authIntegratorPort.auth(
                                realm.getHost(), realm.getExternalUsername(),
                                realm.getExternalPassword(), transactionId
                        );

                        realm.setJwt(authToken.getJwt());
                        realm.setExpirationDate(authToken.getExpirationDate());
                        realm.setRefreshToken(authToken.getRefreshToken());

                        saveRealmPort.save(realm, transactionId);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("[RealmsSchedule][LoginRealm] Error: [{}]", e.getMessage());
            }
        }

    }


    private boolean isExpirationDateExpired(Date expirationDate) {
        return expirationDate.before(new Date());
    }


}
