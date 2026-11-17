package com.trivia.challenge.domain.service;

import com.trivia.challenge.domain.exception.ServiceException;
import com.trivia.challenge.domain.model.CheckedAnswer;
import com.trivia.challenge.domain.model.QuestionId;
import com.trivia.challenge.domain.model.Quiz;
import com.trivia.challenge.domain.model.QuizId;
import com.trivia.challenge.domain.model.QuizQuestion;
import com.trivia.challenge.domain.model.QuizResult;
import com.trivia.challenge.domain.model.QuizStatus;
import com.trivia.challenge.domain.model.SubmittedAnswer;
import com.trivia.challenge.domain.ports.incoming.SubmitQuizUseCase;
import com.trivia.challenge.domain.ports.outgoing.database.QuizStoragePort;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmitQuizService implements SubmitQuizUseCase {

    private final QuizStoragePort quizStoragePort;

    @Override
    public QuizResult submit(QuizId quizId, List<SubmittedAnswer> answers) {
        var quiz = quizStoragePort.findById(quizId);
        if (quiz.status() == QuizStatus.FINISHED) {
            throw new ServiceException("Quiz already finished: " + quiz.id().value());
        }

        var validatedAnswers = validate(quiz, answers);
        var checkedAnswers = quiz.questions().stream()
                .map(question -> {
                    var submittedAnswer = validatedAnswers.get(question.id());
                    var correctAnswer = question.question().correctAnswer();
                    return new CheckedAnswer(
                            question.id(),
                            submittedAnswer,
                            correctAnswer,
                            submittedAnswer != null && correctAnswer.matches(submittedAnswer.submittedAnswer()));
                })
                .toList();

        quiz = quiz.withStatus(QuizStatus.FINISHED);
        var totalQuestions = checkedAnswers.size();
        var correctAnswers = (int)
                checkedAnswers.stream().filter(CheckedAnswer::answeredCorrectly).count();
        var score = totalQuestions == 0 ? 0 : correctAnswers * 100 / totalQuestions;
        var result = new QuizResult(checkedAnswers, totalQuestions, correctAnswers, score);
        quizStoragePort.save(quiz);
        return result;
    }

    private HashMap<QuestionId, SubmittedAnswer> validate(Quiz quiz, List<SubmittedAnswer> submittedAnswers) {
        var answers = new HashMap<QuestionId, SubmittedAnswer>();
        for (var submittedAnswer : submittedAnswers) {
            var questionId = submittedAnswer.questionId();
            var question = findQuestion(quiz, questionId);
            if (!question.containsAnswer(submittedAnswer.submittedAnswer())) {
                throw new ServiceException("Answer is not an option for question: " + questionId.value());
            }
            if (answers.put(questionId, submittedAnswer) != null) {
                throw new ServiceException("Question answered more than once: " + questionId.value());
            }
        }
        return answers;
    }

    private QuizQuestion findQuestion(Quiz quiz, QuestionId questionId) {
        return quiz.questions().stream()
                .filter(question -> question.id().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new ServiceException("Question not part of quiz: " + questionId.value()));
    }
}
