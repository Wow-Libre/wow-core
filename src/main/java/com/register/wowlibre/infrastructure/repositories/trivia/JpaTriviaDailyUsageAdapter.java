package com.register.wowlibre.infrastructure.repositories.trivia;

import com.register.wowlibre.domain.port.out.trivia.ObtainTriviaDailyUsagePort;
import com.register.wowlibre.domain.port.out.trivia.SaveTriviaDailyUsagePort;
import com.register.wowlibre.infrastructure.entities.TriviaDailyUsageEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public class JpaTriviaDailyUsageAdapter implements ObtainTriviaDailyUsagePort, SaveTriviaDailyUsagePort {

    private final TriviaDailyUsageRepository repository;

    public JpaTriviaDailyUsageAdapter(TriviaDailyUsageRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<TriviaDailyUsageEntity> findByUserIdAndUsageDate(Long userId, LocalDate usageDate) {
        return repository.findByUserIdAndUsageDate(userId, usageDate);
    }

    @Override
    public void save(TriviaDailyUsageEntity entity, String transactionId) {
        repository.save(entity);
    }
}
