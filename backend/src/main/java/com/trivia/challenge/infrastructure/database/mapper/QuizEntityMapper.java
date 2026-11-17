package com.trivia.challenge.infrastructure.database.mapper;

import com.trivia.challenge.domain.model.Quiz;
import com.trivia.challenge.domain.model.QuizId;
import com.trivia.challenge.domain.model.QuizQuestion;
import com.trivia.challenge.domain.model.QuizStatus;
import com.trivia.challenge.infrastructure.database.model.QuizEntity;
import com.trivia.challenge.infrastructure.database.model.QuizQuestionEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = QuizQuestionEntityMapper.class)
public interface QuizEntityMapper {

    @Mapping(target = "id", source = "quiz", qualifiedByName = "toId")
    @Mapping(target = "status", source = "quiz", qualifiedByName = "toStatus")
    @Mapping(target = "questions", source = "quiz", qualifiedByName = "toQuestions")
    @Mapping(target = "createdAt", source = "createdAt")
    QuizEntity toQuizEntity(Quiz quiz, Instant createdAt);

    default Quiz toQuiz(QuizEntity entity) {
        return Quiz.restore(
                new QuizId(entity.id()), toQuizQuestions(entity.questions()), QuizStatus.valueOf(entity.status()));
    }

    List<QuizQuestionEntity> toQuizQuestionEntities(List<QuizQuestion> source);

    List<QuizQuestion> toQuizQuestions(List<QuizQuestionEntity> source);

    @Named("toId")
    default UUID toId(Quiz quiz) {
        return quiz.id().value();
    }

    @Named("toQuestions")
    default List<QuizQuestionEntity> toQuestions(Quiz quiz) {
        return toQuizQuestionEntities(quiz.questions());
    }

    @Named("toStatus")
    default String toStatus(Quiz quiz) {
        return quiz.status().name();
    }
}
