package com.register.wowlibre.domain.port.in.machine;

import com.register.wowlibre.domain.dto.*;

import java.util.*;

public interface MachinePort {
    MachineDto evaluate(Long userId, Long accountId, Long characterId, Long realmId, Locale locale,
                        String transactionId);

    MachineDetailDto points(Long userId, Long accountId, Long realmId, String transactionId);

    void changePoints(Long userId, Long accountId, Long characterId, Long realmId, Long points, String type,
                      String transactionId);

    void addPointsSubscription(Long userId, Integer points, String transactionId);

    /**
     * Añade puntos de ruleta desde el minijuego de Telegram.
     * Usa el reino elegido por el usuario en la sesión del bot.
     *
     * @return true si se añadieron puntos; false si no se pudo (reino inexistente, etc.).
     */
    boolean addRuletaPointsFromTelegram(Long userId, Long realmId, int points, String transactionId);
}
