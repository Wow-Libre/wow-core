package com.register.wowlibre.infrastructure.repositories.trivia;

import com.register.wowlibre.infrastructure.entities.TriviaQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TriviaQuestionRepository extends JpaRepository<TriviaQuestionEntity, Long> {

    @Query(value = "SELECT * FROM trivia_question WHERE active = true ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<TriviaQuestionEntity> findRandomActive();
}
