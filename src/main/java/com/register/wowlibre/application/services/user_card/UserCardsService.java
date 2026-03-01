package com.register.wowlibre.application.services.user_card;

import com.register.wowlibre.domain.dto.user_card.CardItemDto;
import com.register.wowlibre.domain.port.in.user_card.UserCardsPort;
import com.register.wowlibre.domain.port.out.user_card.ObtainCardCatalog;
import com.register.wowlibre.domain.port.out.user_card.ObtainUserCards;
import com.register.wowlibre.infrastructure.entities.UserCardEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserCardsService implements UserCardsPort {

    private final ObtainUserCards obtainUserCards;
    private final ObtainCardCatalog obtainCardCatalog;

    public UserCardsService(ObtainUserCards obtainUserCards, ObtainCardCatalog obtainCardCatalog) {
        this.obtainUserCards = obtainUserCards;
        this.obtainCardCatalog = obtainCardCatalog;
    }

    @Override
    public List<CardItemDto> getDiscoveredCards(Long userId, String transactionId) {
        List<UserCardEntity> entities = obtainUserCards.findByUserId(userId, transactionId);
        List<String> codes = entities.stream().map(UserCardEntity::getCardCode).collect(Collectors.toList());
        List<CardItemDto> catalogItems = obtainCardCatalog.findByCodes(codes);
        Map<String, CardItemDto> catalogByCode = catalogItems.stream()
                .collect(Collectors.toMap(CardItemDto::getCode, Function.identity(), (a, b) -> a));
        return entities.stream()
                .map(e -> {
                    String code = e.getCardCode();
                    CardItemDto catalog = catalogByCode.get(code);
                    return new CardItemDto(
                            code,
                            catalog != null ? catalog.getImageUrl() : null,
                            catalog != null ? catalog.getName() : null);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CardItemDto> getCatalog() {
        return obtainCardCatalog.findAllForDisplay();
    }
}
