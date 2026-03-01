package com.register.wowlibre.application.services.user_card;

import com.register.wowlibre.domain.dto.user_card.CardItemDto;
import com.register.wowlibre.domain.dto.user_card.CardWithProbabilityDto;
import com.register.wowlibre.domain.exception.BadRequestException;
import com.register.wowlibre.domain.port.in.user_card.BuyPackPort;
import com.register.wowlibre.domain.port.in.wallet.WalletPort;
import com.register.wowlibre.domain.port.out.user_card.ObtainCardCatalog;
import com.register.wowlibre.domain.port.out.user_card.SaveUserCards;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class BuyPackService implements BuyPackPort {

    private static final long PACK_COST_POINTS = 200L;
    private static final int CARDS_PER_PACK = 3;

    private final WalletPort walletPort;
    private final ObtainCardCatalog obtainCardCatalog;
    private final SaveUserCards saveUserCards;

    public BuyPackService(WalletPort walletPort, ObtainCardCatalog obtainCardCatalog, SaveUserCards saveUserCards) {
        this.walletPort = walletPort;
        this.obtainCardCatalog = obtainCardCatalog;
        this.saveUserCards = saveUserCards;
    }

    @Override
    public List<CardItemDto> buyPack(Long userId, String transactionId) {
        walletPort.deductPoints(userId, PACK_COST_POINTS, transactionId);

        List<CardWithProbabilityDto> catalog = obtainCardCatalog.findAllWithProbability();
        if (catalog == null || catalog.isEmpty()) {
            throw new BadRequestException("Card catalog is empty", transactionId);
        }

        List<String> drawnCodes = drawWeightedRandom(catalog, CARDS_PER_PACK);
        saveUserCards.addOrIncrement(userId, drawnCodes);

        java.util.Map<String, Long> countInPack = drawnCodes.stream()
                .collect(Collectors.groupingBy(java.util.function.Function.identity(), Collectors.counting()));
        List<CardItemDto> catalogItems = obtainCardCatalog.findByCodes(new ArrayList<>(countInPack.keySet()));
        return catalogItems.stream()
                .map(c -> new CardItemDto(c.getCode(), c.getImageUrl(), c.getName(), countInPack.get(c.getCode()).intValue()))
                .collect(Collectors.toList());
    }

    /**
     * Sorteo ponderado con reemplazo: cada carta tiene probabilidad 1-100.
     * Suma total de pesos; random en [0, sum); recorre restando hasta que sea < 0.
     */
    private List<String> drawWeightedRandom(List<CardWithProbabilityDto> catalog, int count) {
        int totalWeight = catalog.stream()
                .mapToInt(c -> Math.max(1, Math.min(100, c.getProbability())))
                .sum();
        if (totalWeight <= 0) {
            totalWeight = 1;
        }

        List<String> result = new ArrayList<>(count);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = 0; i < count; i++) {
            int r = rnd.nextInt(totalWeight);
            int acc = 0;
            for (CardWithProbabilityDto c : catalog) {
                int w = Math.max(1, Math.min(100, c.getProbability()));
                acc += w;
                if (r < acc) {
                    result.add(c.getCode());
                    break;
                }
            }
            if (result.size() <= i) {
                result.add(catalog.get(catalog.size() - 1).getCode());
            }
        }
        return result;
    }
}
