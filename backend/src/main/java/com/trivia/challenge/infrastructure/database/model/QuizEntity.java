package com.trivia.challenge.infrastructure.database.model;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record QuizEntity(UUID id, String status, List<QuizQuestionEntity> questions, Instant createdAt) {

    public QuizEntity {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        questions = List.copyOf(questions);
    }

    public QuizEntity withCreatedAt(Instant createdAt) {
        return new QuizEntity(id, status, questions, createdAt);
    }

    public boolean isExpired(Instant now, Duration ttl) {
        return !now.isBefore(createdAt.plus(ttl));
    }
}
