package com.trivia.challenge.infrastructure.database.model;

import java.util.List;
import java.util.Objects;

public record QuizQuestionEntity(QuestionEntity question, List<String> possibleAnswers) {

    public QuizQuestionEntity {
        Objects.requireNonNull(question, "question must not be null");
        Objects.requireNonNull(possibleAnswers, "possibleAnswers must not be null");
        possibleAnswers = List.copyOf(possibleAnswers);
    }
}
