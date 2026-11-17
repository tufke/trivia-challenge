package com.trivia.challenge.infrastructure.database.model;

import java.util.List;
import java.util.UUID;

public record QuestionEntity(
        UUID id,
        String category,
        String type,
        String difficulty,
        String question,
        String correctAnswer,
        List<String> incorrectAnswers) {

    public QuestionEntity {
        incorrectAnswers = List.copyOf(incorrectAnswers);
    }
}
