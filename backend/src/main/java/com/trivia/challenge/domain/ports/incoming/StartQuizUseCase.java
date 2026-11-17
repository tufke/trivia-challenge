package com.trivia.challenge.domain.ports.incoming;

import com.trivia.challenge.domain.model.Quiz;

@FunctionalInterface
public interface StartQuizUseCase {

    Quiz startQuiz(int amount);
}
