package com.register.wowlibre.application.services.trivia;

import com.register.wowlibre.domain.dto.TriviaQuestionDto;
import com.register.wowlibre.domain.port.in.trivia.TriviaPort;
import com.register.wowlibre.domain.port.in.wallet.WalletPort;
import com.register.wowlibre.domain.port.out.trivia.ObtainTriviaDailyCreatePort;
import com.register.wowlibre.domain.port.out.trivia.ObtainTriviaDailyUsagePort;
import com.register.wowlibre.domain.port.out.trivia.ObtainTriviaQuestionPort;
import com.register.wowlibre.domain.port.out.trivia.ObtainTriviaQuestionRatingPort;
import com.register.wowlibre.domain.port.out.trivia.SaveTriviaDailyCreatePort;
import com.register.wowlibre.domain.port.out.trivia.SaveTriviaDailyUsagePort;
import com.register.wowlibre.domain.port.out.trivia.SaveTriviaQuestionPort;
import com.register.wowlibre.domain.port.out.trivia.SaveTriviaQuestionRatingPort;
import com.register.wowlibre.infrastructure.entities.TriviaDailyCreateEntity;
import com.register.wowlibre.infrastructure.entities.TriviaDailyUsageEntity;
import com.register.wowlibre.infrastructure.entities.TriviaQuestionEntity;
import com.register.wowlibre.infrastructure.entities.TriviaQuestionRatingEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class TriviaService implements TriviaPort {

    public static final int DAILY_QUESTION_LIMIT = 10;
    public static final int DAILY_CREATE_LIMIT = 10;
    public static final int POINTS_FOR_CREATING_QUESTION = 1;
    public static final int POINTS_DEDUCTED_FOR_NEGATIVE_RATING = 2;

    private final ObtainTriviaQuestionPort obtainTriviaQuestionPort;
    private final SaveTriviaQuestionPort saveTriviaQuestionPort;
    private final ObtainTriviaDailyUsagePort obtainTriviaDailyUsagePort;
    private final SaveTriviaDailyUsagePort saveTriviaDailyUsagePort;
    private final ObtainTriviaDailyCreatePort obtainTriviaDailyCreatePort;
    private final SaveTriviaDailyCreatePort saveTriviaDailyCreatePort;
    private final ObtainTriviaQuestionRatingPort obtainTriviaQuestionRatingPort;
    private final SaveTriviaQuestionRatingPort saveTriviaQuestionRatingPort;
    private final WalletPort walletPort;

    public TriviaService(ObtainTriviaQuestionPort obtainTriviaQuestionPort,
                         SaveTriviaQuestionPort saveTriviaQuestionPort,
                         ObtainTriviaDailyUsagePort obtainTriviaDailyUsagePort,
                         SaveTriviaDailyUsagePort saveTriviaDailyUsagePort,
                         ObtainTriviaDailyCreatePort obtainTriviaDailyCreatePort,
                         SaveTriviaDailyCreatePort saveTriviaDailyCreatePort,
                         ObtainTriviaQuestionRatingPort obtainTriviaQuestionRatingPort,
                         SaveTriviaQuestionRatingPort saveTriviaQuestionRatingPort,
                         WalletPort walletPort) {
        this.obtainTriviaQuestionPort = obtainTriviaQuestionPort;
        this.saveTriviaQuestionPort = saveTriviaQuestionPort;
        this.obtainTriviaDailyUsagePort = obtainTriviaDailyUsagePort;
        this.saveTriviaDailyUsagePort = saveTriviaDailyUsagePort;
        this.obtainTriviaDailyCreatePort = obtainTriviaDailyCreatePort;
        this.saveTriviaDailyCreatePort = saveTriviaDailyCreatePort;
        this.obtainTriviaQuestionRatingPort = obtainTriviaQuestionRatingPort;
        this.saveTriviaQuestionRatingPort = saveTriviaQuestionRatingPort;
        this.walletPort = walletPort;
    }

    @Override
    public Optional<TriviaQuestionDto> getRandomQuestion(String transactionId) {
        return obtainTriviaQuestionPort.findRandomActive(transactionId)
                .map(this::toDto);
    }

    @Override
    public boolean canAskQuestionToday(Long userId, String transactionId) {
        LocalDate today = LocalDate.now();
        int count = obtainTriviaDailyUsagePort.findByUserIdAndUsageDate(userId, today)
                .map(TriviaDailyUsageEntity::getCount)
                .orElse(0);
        return count < DAILY_QUESTION_LIMIT;
    }

    @Override
    public void recordQuestionAsked(Long userId, String transactionId) {
        LocalDate today = LocalDate.now();
        TriviaDailyUsageEntity usage = obtainTriviaDailyUsagePort.findByUserIdAndUsageDate(userId, today)
                .orElseGet(() -> {
                    TriviaDailyUsageEntity newUsage = new TriviaDailyUsageEntity();
                    newUsage.setUserId(userId);
                    newUsage.setUsageDate(today);
                    newUsage.setCount(0);
                    return newUsage;
                });
        usage.setCount(usage.getCount() + 1);
        saveTriviaDailyUsagePort.save(usage, transactionId);
    }

    @Override
    public int getRemainingQuestionsToday(Long userId, String transactionId) {
        LocalDate today = LocalDate.now();
        int count = obtainTriviaDailyUsagePort.findByUserIdAndUsageDate(userId, today)
                .map(TriviaDailyUsageEntity::getCount)
                .orElse(0);
        return Math.max(0, DAILY_QUESTION_LIMIT - count);
    }

    @Override
    public boolean canCreateQuestionToday(Long userId, String transactionId) {
        LocalDate today = LocalDate.now();
        int count = obtainTriviaDailyCreatePort.findByUserIdAndUsageDate(userId, today)
                .map(TriviaDailyCreateEntity::getCount)
                .orElse(0);
        return count < DAILY_CREATE_LIMIT;
    }

    @Override
    public int getRemainingCreatesToday(Long userId, String transactionId) {
        LocalDate today = LocalDate.now();
        int count = obtainTriviaDailyCreatePort.findByUserIdAndUsageDate(userId, today)
                .map(TriviaDailyCreateEntity::getCount)
                .orElse(0);
        return Math.max(0, DAILY_CREATE_LIMIT - count);
    }

    @Override
    public void createQuestion(Long createdByUserId, String questionText, String optionA, String optionB,
                               String optionC, String optionD, String correctOption, String transactionId) {
        if (!canCreateQuestionToday(createdByUserId, transactionId)) {
            throw new IllegalStateException("Daily create limit reached");
        }
        TriviaQuestionEntity entity = new TriviaQuestionEntity();
        entity.setQuestionText(questionText);
        entity.setOptionA(optionA);
        entity.setOptionB(optionB);
        entity.setOptionC(optionC);
        entity.setOptionD(optionD);
        entity.setCorrectOption(correctOption != null ? correctOption.trim().toUpperCase().substring(0, 1) : "A");
        entity.setActive(true);
        entity.setCreatedByUserId(createdByUserId);
        saveTriviaQuestionPort.save(entity, transactionId);
        recordQuestionCreated(createdByUserId, transactionId);
        walletPort.addPoints(createdByUserId, (long) POINTS_FOR_CREATING_QUESTION, transactionId);
    }

    private void recordQuestionCreated(Long userId, String transactionId) {
        LocalDate today = LocalDate.now();
        TriviaDailyCreateEntity usage = obtainTriviaDailyCreatePort.findByUserIdAndUsageDate(userId, today)
                .orElseGet(() -> {
                    TriviaDailyCreateEntity newUsage = new TriviaDailyCreateEntity();
                    newUsage.setUserId(userId);
                    newUsage.setUsageDate(today);
                    newUsage.setCount(0);
                    return newUsage;
                });
        usage.setCount(usage.getCount() + 1);
        saveTriviaDailyCreatePort.save(usage, transactionId);
    }

    @Override
    public void rateQuestion(Long questionId, Long userId, boolean isPositive, String transactionId) {
        if (obtainTriviaQuestionRatingPort.findByQuestionIdAndUserId(questionId, userId).isPresent()) {
            return;
        }
        TriviaQuestionRatingEntity rating = new TriviaQuestionRatingEntity();
        rating.setQuestionId(questionId);
        rating.setUserId(userId);
        rating.setPositive(isPositive);
        saveTriviaQuestionRatingPort.save(rating, transactionId);
        if (!isPositive) {
            obtainTriviaQuestionPort.findById(questionId, transactionId)
                    .map(TriviaQuestionEntity::getCreatedByUserId)
                    .filter(creatorId -> creatorId != null)
                    .ifPresent(creatorId -> walletPort.addPoints(creatorId, (long) -POINTS_DEDUCTED_FOR_NEGATIVE_RATING, transactionId));
        }
    }

    @Override
    public boolean hasRatedQuestion(Long questionId, Long userId, String transactionId) {
        return obtainTriviaQuestionRatingPort.findByQuestionIdAndUserId(questionId, userId).isPresent();
    }

    private TriviaQuestionDto toDto(TriviaQuestionEntity e) {
        return new TriviaQuestionDto(
                e.getId(),
                e.getQuestionText(),
                e.getOptionA(),
                e.getOptionB(),
                e.getOptionC(),
                e.getOptionD(),
                e.getCorrectOption()
        );
    }
}
