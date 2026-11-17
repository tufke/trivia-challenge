package com.trivia.challenge.domain.model;

import java.util.Objects;
import org.jspecify.annotations.Nullable;

public record CheckedAnswer(
        QuestionId questionId,
        @Nullable SubmittedAnswer submittedAnswer,
        Answer correctAnswer,
        boolean answeredCorrectly) {

    public CheckedAnswer {
        Objects.requireNonNull(questionId, "questionId must not be null");
        Objects.requireNonNull(correctAnswer, "correctAnswer must not be null");
    }
}
