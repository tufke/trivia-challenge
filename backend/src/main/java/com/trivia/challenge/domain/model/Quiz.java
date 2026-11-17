package com.trivia.challenge.domain.model;

import java.util.List;
import java.util.Objects;

public record Quiz(QuizId id, List<QuizQuestion> questions, QuizStatus status) {

    public Quiz {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(questions, "questions must not be null");
        Objects.requireNonNull(status, "status must not be null");
        questions = List.copyOf(questions);
    }

    public static Quiz restore(QuizId id, List<QuizQuestion> questions, QuizStatus status) {
        return new Quiz(id, questions, status);
    }

    public Quiz withStatus(QuizStatus status) {
        return new Quiz(id, questions, status);
    }
}
