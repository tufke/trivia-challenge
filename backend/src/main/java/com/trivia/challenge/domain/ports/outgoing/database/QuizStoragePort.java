package com.trivia.challenge.domain.ports.outgoing.database;

import com.trivia.challenge.domain.model.Quiz;
import com.trivia.challenge.domain.model.QuizId;

public interface QuizStoragePort {

    void save(Quiz quiz);

    Quiz findById(QuizId quizId);
}
