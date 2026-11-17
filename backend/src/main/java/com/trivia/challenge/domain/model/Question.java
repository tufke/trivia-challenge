package com.trivia.challenge.domain.model;

import com.trivia.challenge.domain.exception.ServiceException;
import java.util.List;
import java.util.Objects;

public record Question(
        QuestionId id,
        Category category,
        QuestionType type,
        Difficulty difficulty,
        String text,
        Answer correctAnswer,
        List<Answer> incorrectAnswers) {

    public Question {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(category, "category must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(difficulty, "difficulty must not be null");
        Objects.requireNonNull(text, "text must not be null");
        Objects.requireNonNull(correctAnswer, "correctAnswer must not be null");
        incorrectAnswers = List.copyOf(incorrectAnswers);
        if (incorrectAnswers.isEmpty()) {
            throw new ServiceException("A question requires at least one incorrect submittedAnswer");
        }
    }
}
