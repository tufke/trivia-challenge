package com.trivia.challenge.infrastructure.database.cache;

import com.trivia.challenge.infrastructure.database.config.TriviaQuizCacheProperties;
import com.trivia.challenge.infrastructure.database.model.QuizEntity;
import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InMemoryQuizCache {

    private final TriviaQuizCacheProperties properties;
    private final Clock clock;

    private final Map<UUID, QuizEntity> quizzes = new ConcurrentHashMap<>();

    public void save(QuizEntity entity) {
        quizzes.merge(entity.id(), entity, (existing, updated) -> updated.withCreatedAt(existing.createdAt()));
    }

    public Optional<QuizEntity> findById(UUID id) {
        return Optional.ofNullable(quizzes.get(id))
                .filter(entity -> !entity.isExpired(clock.instant(), properties.quizTtl()));
    }

    public int removeExpiredQuizzes() {
        var now = clock.instant();
        var before = quizzes.size();
        quizzes.values().removeIf(entity -> entity.isExpired(now, properties.quizTtl()));
        return before - quizzes.size();
    }

    public int size() {
        return quizzes.size();
    }
}
