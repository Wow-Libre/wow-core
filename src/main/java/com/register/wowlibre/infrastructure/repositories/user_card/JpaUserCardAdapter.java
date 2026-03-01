package com.register.wowlibre.infrastructure.repositories.user_card;

import com.register.wowlibre.domain.port.out.user_card.DecrementUserCard;
import com.register.wowlibre.domain.port.out.user_card.ObtainUserCards;
import com.register.wowlibre.domain.port.out.user_card.SaveUserCards;
import com.register.wowlibre.infrastructure.entities.UserCardEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaUserCardAdapter implements ObtainUserCards, SaveUserCards, DecrementUserCard {

    private final UserCardRepository userCardRepository;

    public JpaUserCardAdapter(UserCardRepository userCardRepository) {
        this.userCardRepository = userCardRepository;
    }

    @Override
    public List<UserCardEntity> findByUserId(Long userId, String transactionId) {
        return userCardRepository.findByUserIdOrderByObtainedAtDesc(userId);
    }

    @Override
    public void saveAll(List<UserCardEntity> entities) {
        if (entities != null && !entities.isEmpty()) {
            userCardRepository.saveAll(entities);
        }
    }

    @Override
    public void addOrIncrement(Long userId, List<String> cardCodes) {
        if (userId == null || cardCodes == null || cardCodes.isEmpty()) return;
        for (String code : cardCodes) {
            userCardRepository.findByUserIdAndCardCode(userId, code)
                    .ifPresentOrElse(
                            e -> {
                                e.setQuantity(e.getQuantity() != null ? e.getQuantity() + 1 : 1);
                                userCardRepository.save(e);
                            },
                            () -> {
                                UserCardEntity entity = new UserCardEntity();
                                entity.setUserId(userId);
                                entity.setCardCode(code);
                                entity.setQuantity(1);
                                userCardRepository.save(entity);
                            });
        }
    }

    @Override
    public boolean decrement(Long userId, String cardCode) {
        return userCardRepository.findByUserIdAndCardCode(userId, cardCode)
                .map(e -> {
                    int qty = e.getQuantity() != null ? e.getQuantity() : 1;
                    if (qty <= 1) {
                        userCardRepository.delete(e);
                    } else {
                        e.setQuantity(qty - 1);
                        userCardRepository.save(e);
                    }
                    return true;
                })
                .orElse(false);
    }
}
