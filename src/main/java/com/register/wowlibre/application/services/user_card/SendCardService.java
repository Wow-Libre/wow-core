package com.register.wowlibre.application.services.user_card;

import com.register.wowlibre.domain.exception.BadRequestException;
import com.register.wowlibre.domain.port.in.user_card.SendCardPort;
import com.register.wowlibre.domain.port.in.user.UserPort;
import com.register.wowlibre.domain.port.out.user_card.DecrementUserCard;
import com.register.wowlibre.domain.port.out.user_card.SaveUserCards;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SendCardService implements SendCardPort {

    private final DecrementUserCard decrementUserCard;
    private final SaveUserCards saveUserCards;
    private final UserPort userPort;

    public SendCardService(DecrementUserCard decrementUserCard, SaveUserCards saveUserCards, UserPort userPort) {
        this.decrementUserCard = decrementUserCard;
        this.saveUserCards = saveUserCards;
        this.userPort = userPort;
    }

    @Override
    public void sendCard(Long fromUserId, String toUserEmail, String cardCode, String transactionId) {
        if (fromUserId == null || toUserEmail == null || toUserEmail.isBlank() || cardCode == null || cardCode.isBlank()) {
            throw new BadRequestException("Datos incompletos para enviar la carta (correo del destinatario y código de carta)", transactionId);
        }
        Long toUserId = userPort.findByEmailEntity(toUserEmail.trim(), transactionId)
                .map(com.register.wowlibre.infrastructure.entities.UserEntity::getId)
                .orElseThrow(() -> new BadRequestException("Usuario destino no encontrado con ese correo", transactionId));
        if (fromUserId.equals(toUserId)) {
            throw new BadRequestException("No puedes enviarte una carta a ti mismo", transactionId);
        }
        if (!decrementUserCard.decrement(fromUserId, cardCode)) {
            throw new BadRequestException("No tienes esa carta o no tienes copias disponibles", transactionId);
        }
        saveUserCards.addOrIncrement(toUserId, Collections.singletonList(cardCode));
    }
}
