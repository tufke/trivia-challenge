package com.trivia.challenge.infrastructure.database;

import com.trivia.challenge.domain.exception.ServiceException;
import com.trivia.challenge.domain.model.Quiz;
import com.trivia.challenge.domain.model.QuizId;
import com.trivia.challenge.domain.ports.outgoing.database.QuizStoragePort;
import com.trivia.challenge.infrastructure.database.cache.InMemoryQuizCache;
import com.trivia.challenge.infrastructure.database.mapper.QuizEntityMapper;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizStorageAdapter implements QuizStoragePort {

    private final InMemoryQuizCache inMemoryQuizCache;
    private final QuizEntityMapper quizEntityMapper;
    private final Clock clock;

    @Override
    public void save(Quiz quiz) {
        inMemoryQuizCache.save(quizEntityMapper.toQuizEntity(quiz, clock.instant()));
    }

    @Override
    public Quiz findById(QuizId quizId) {
        return inMemoryQuizCache
                .findById(quizId.value())
                .map(quizEntityMapper::toQuiz)
                .orElseThrow(() -> new ServiceException("Quiz not found: " + quizId.value()));
    }
}
