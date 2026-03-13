package com.register.wowlibre.domain.port.in.user_card;

import com.register.wowlibre.domain.dto.user_card.CardCatalogAdminDto;
import com.register.wowlibre.domain.dto.user_card.CardCatalogAdminRequestDto;

import java.util.List;

public interface CardCatalogAdminPort {

    List<CardCatalogAdminDto> listAll(String transactionId);

    CardCatalogAdminDto create(CardCatalogAdminRequestDto request, String transactionId);

    CardCatalogAdminDto update(String code, CardCatalogAdminRequestDto request, String transactionId);

    void delete(String code, String transactionId);
}
