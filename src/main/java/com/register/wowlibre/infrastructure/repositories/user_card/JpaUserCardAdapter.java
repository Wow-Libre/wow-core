package com.register.wowlibre.infrastructure.repositories.user_card;

import com.register.wowlibre.domain.port.out.user_card.ObtainUserCards;
import com.register.wowlibre.domain.port.out.user_card.SaveUserCards;
import com.register.wowlibre.infrastructure.entities.UserCardEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaUserCardAdapter implements ObtainUserCards, SaveUserCards {

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
}
