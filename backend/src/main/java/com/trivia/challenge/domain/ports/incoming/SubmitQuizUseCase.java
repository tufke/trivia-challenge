package com.trivia.challenge.domain.ports.incoming;

import com.trivia.challenge.domain.model.QuizId;
import com.trivia.challenge.domain.model.QuizResult;
import com.trivia.challenge.domain.model.SubmittedAnswer;
import java.util.List;

@FunctionalInterface
public interface SubmitQuizUseCase {

    QuizResult submit(QuizId quizId, List<SubmittedAnswer> answers);
}
