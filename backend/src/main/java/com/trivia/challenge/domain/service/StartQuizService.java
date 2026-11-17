package com.trivia.challenge.domain.service;

import com.trivia.challenge.domain.exception.ServiceException;
import com.trivia.challenge.domain.model.Answer;
import com.trivia.challenge.domain.model.Question;
import com.trivia.challenge.domain.model.QuestionId;
import com.trivia.challenge.domain.model.Quiz;
import com.trivia.challenge.domain.model.QuizId;
import com.trivia.challenge.domain.model.QuizQuestion;
import com.trivia.challenge.domain.model.QuizStatus;
import com.trivia.challenge.domain.ports.incoming.StartQuizUseCase;
import com.trivia.challenge.domain.ports.outgoing.database.QuestionsStoragePort;
import com.trivia.challenge.domain.ports.outgoing.database.QuizStoragePort;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StartQuizService implements StartQuizUseCase {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final QuestionsStoragePort questionsStoragePort;
    private final QuizStoragePort quizStoragePort;

    @Override
    public Quiz startQuiz(int amount) {
        var questions = questionsStoragePort.getRandomQuestions(amount);
        Objects.requireNonNull(questions, "questions must not be null");
        if (questions.isEmpty()) {
            throw new ServiceException("A quiz requires at least one question");
        }
        var questionIds = new HashSet<QuestionId>();
        for (var question : questions) {
            if (!questionIds.add(question.id())) {
                throw new ServiceException(
                        "Question included more than once: " + question.id().value());
            }
        }
        var quiz = Quiz.restore(
                QuizId.generate(),
                questions.stream()
                        .map(question -> new QuizQuestion(question, answerOptions(question)))
                        .toList(),
                QuizStatus.IN_PROGRESS);
        quizStoragePort.save(quiz);
        return quiz;
    }

    private List<Answer> answerOptions(Question question) {
        var options = new ArrayList<Answer>(question.incorrectAnswers().size() + 1);
        options.add(question.correctAnswer());
        options.addAll(question.incorrectAnswers());
        switch (question.type()) {
            case BOOLEAN -> options.sort(Comparator.comparing(Answer::text).reversed());
            case MULTIPLE -> Collections.shuffle(options, RANDOM);
        }
        return List.copyOf(options);
    }
}
