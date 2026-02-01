package com.register.wowlibre.infrastructure.repositories.trivia;

import com.register.wowlibre.domain.port.out.trivia.ObtainTriviaDailyCreatePort;
import com.register.wowlibre.domain.port.out.trivia.SaveTriviaDailyCreatePort;
import com.register.wowlibre.infrastructure.entities.TriviaDailyCreateEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public class JpaTriviaDailyCreateAdapter implements ObtainTriviaDailyCreatePort, SaveTriviaDailyCreatePort {

    private final TriviaDailyCreateRepository repository;

    public JpaTriviaDailyCreateAdapter(TriviaDailyCreateRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<TriviaDailyCreateEntity> findByUserIdAndUsageDate(Long userId, LocalDate usageDate) {
        return repository.findByUserIdAndUsageDate(userId, usageDate);
    }

    @Override
    public void save(TriviaDailyCreateEntity entity, String transactionId) {
        repository.save(entity);
    }
}
