package com.trivia.challenge.domain.model;

import java.util.Objects;
import java.util.UUID;

public record QuestionId(UUID value) {

    public QuestionId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static QuestionId generate() {
        return new QuestionId(UUID.randomUUID());
    }
}
