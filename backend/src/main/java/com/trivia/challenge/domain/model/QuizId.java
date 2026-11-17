package com.trivia.challenge.domain.model;

import java.util.Objects;
import java.util.UUID;

public record QuizId(UUID value) {

    public QuizId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static QuizId generate() {
        return new QuizId(UUID.randomUUID());
    }
}
