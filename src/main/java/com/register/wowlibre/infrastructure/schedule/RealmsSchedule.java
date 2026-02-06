package com.register.wowlibre.infrastructure.schedule;

import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.port.in.auth_integrator.*;
import com.register.wowlibre.domain.port.out.realm.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.util.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
public class RealmsSchedule {
    private static final Logger LOGGER = LoggerFactory.getLogger(RealmsSchedule.class);

    private final ObtainRealmPort obtainRealmPort;
    private final SaveRealmPort saveRealmPort;
    private final AuthIntegratorPort authIntegratorPort;
    private final RandomString randomString;

    public RealmsSchedule(ObtainRealmPort obtainRealmPort, AuthIntegratorPort authIntegratorPort,
                          SaveRealmPort saveRealmPort, @Qualifier("resetPasswordString") RandomString randomString) {
        this.obtainRealmPort = obtainRealmPort;
        this.authIntegratorPort = authIntegratorPort;
        this.saveRealmPort = saveRealmPort;
        this.randomString = randomString;
    }

    @Scheduled(cron = "1 0/5 * * * *")
    public void LoginRealm() {
        final String transactionId = "[RealmsSchedule][LoginRealm]";
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

    @Scheduled(cron = "1 0/1 * * * *")
    public void vinculatedRealm() {
        final String transactionId = "[RealmsSchedule][vinculatedRealm]";
        List<RealmEntity> realms = obtainRealmPort.findByStatusIsFalseAndRetry(5L, transactionId);

        for (RealmEntity realm : realms) {
            LOGGER.info("[RealmsSchedule][vinculatedRealm] Start authentication generation process {} ",
                    realm.getName());

            try {
                final String gmUsername = realm.getGmUsername();
                final String gmPassword = realm.getGmPassword();

                final String usernameRealm = String.format("%s-%s", realm.getName(), realm.getExpansionId());
                final String passwordRealm = randomString.nextString();

                // CREATE USER REALM
                authIntegratorPort.create(realm.getHost(), usernameRealm, passwordRealm,
                        realm.getApiKey(), realm.getEmulator(), realm.getExpansionId(), gmUsername, gmPassword,
                        transactionId);

                // LOGIN
                AuthClientResponse authToken = authIntegratorPort.auth(
                        realm.getHost(), usernameRealm, passwordRealm, transactionId
                );

                realm.setJwt(authToken.getJwt());
                realm.setExpirationDate(adjustExpirationDate(authToken.getExpirationDate()));
                realm.setRefreshToken(authToken.getRefreshToken());
                realm.setStatus(true);
                realm.setRetry(0);
                realm.setExternalUsername(usernameRealm);
                realm.setExternalPassword(passwordRealm);
                realm.setGmPassword(null);
                realm.setGmUsername(null);
                saveRealmPort.save(realm, transactionId);
                LOGGER.info("The kingdom is linked correctly {} ", realm.getName());
            } catch (Exception e) {
                LOGGER.error("[RealmsSchedule][vinculatedRealm] an unexpected error has occurred Error: {}",
                        e.getMessage());
                realm.setRetry(realm.getRetry() == null ? 0 : realm.getRetry() + 1);
                saveRealmPort.save(realm, transactionId);
            }
        }

    }

    private boolean isExpirationDateExpired(Date expirationDate) {
        return expirationDate.before(new Date());
    }

    private Date adjustExpirationDate(Date expirationDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expirationDate);
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        return calendar.getTime();
    }

}
