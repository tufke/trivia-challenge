package com.trivia.challenge.domain.model;

import java.util.List;

public record QuizResult(List<CheckedAnswer> checkedAnswers, int totalQuestions, int correctAnswers, int score) {

    public QuizResult {
        checkedAnswers = List.copyOf(checkedAnswers);
    }
}
