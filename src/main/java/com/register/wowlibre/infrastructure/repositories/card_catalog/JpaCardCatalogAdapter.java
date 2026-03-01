package com.register.wowlibre.infrastructure.repositories.card_catalog;

import com.register.wowlibre.domain.dto.user_card.CardItemDto;
import com.register.wowlibre.domain.dto.user_card.CardWithProbabilityDto;
import com.register.wowlibre.domain.port.out.user_card.ObtainCardCatalog;
import com.register.wowlibre.infrastructure.entities.CardCatalogEntity;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class JpaCardCatalogAdapter implements ObtainCardCatalog {

    private final CardCatalogRepository cardCatalogRepository;

    public JpaCardCatalogAdapter(CardCatalogRepository cardCatalogRepository) {
        this.cardCatalogRepository = cardCatalogRepository;
    }

    @Override
    public List<CardItemDto> findByCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        }
        List<CardCatalogEntity> entities = cardCatalogRepository.findByCodeIn(codes);
        return entities.stream()
                .map(e -> new CardItemDto(e.getCode(), e.getImageUrl(), e.getDisplayName(), null))
                .collect(Collectors.toList());
    }

    @Override
    public List<CardWithProbabilityDto> findAllWithProbability() {
        return cardCatalogRepository.findAll().stream()
                .map(e -> new CardWithProbabilityDto(e.getCode(), e.getProbability() != null ? e.getProbability() : 50))
                .collect(Collectors.toList());
    }

    @Override
    public List<CardItemDto> findAllForDisplay() {
        return cardCatalogRepository.findAll().stream()
                .map(e -> new CardItemDto(e.getCode(), e.getImageUrl(), e.getDisplayName(), null))
                .collect(Collectors.toList());
    }
}
