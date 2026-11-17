package com.trivia.challenge.domain.model;

import java.util.List;
import java.util.Objects;

public record QuizQuestion(Question question, List<Answer> possibleAnswers) {

    public QuizQuestion {
        Objects.requireNonNull(question, "question must not be null");
        possibleAnswers = List.copyOf(possibleAnswers);
    }

    public QuestionId id() {
        return question.id();
    }

    public boolean containsAnswer(Answer answerToCheck) {
        return possibleAnswers.stream().anyMatch(answer -> answer.matches(answerToCheck));
    }
}
