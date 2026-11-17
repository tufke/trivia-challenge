package com.trivia.challenge.domain.model;

import java.util.Objects;

public record SubmittedAnswer(QuestionId questionId, Answer submittedAnswer) {

    public SubmittedAnswer {
        Objects.requireNonNull(questionId, "questionId must not be null");
        Objects.requireNonNull(submittedAnswer, "submittedAnswer must not be null");
    }
}
