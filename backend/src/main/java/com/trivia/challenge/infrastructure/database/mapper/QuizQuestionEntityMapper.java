package com.trivia.challenge.infrastructure.database.mapper;

import com.trivia.challenge.domain.model.Answer;
import com.trivia.challenge.domain.model.QuizQuestion;
import com.trivia.challenge.infrastructure.database.model.QuizQuestionEntity;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = QuestionEntityMapper.class)
public interface QuizQuestionEntityMapper {

    @Mapping(target = "possibleAnswers", source = "possibleAnswers", qualifiedByName = "toPossibleAnswerTexts")
    QuizQuestionEntity toQuizQuestionEntity(QuizQuestion source);

    @Mapping(target = "possibleAnswers", source = "possibleAnswers", qualifiedByName = "toPossibleAnswers")
    QuizQuestion toQuizQuestion(QuizQuestionEntity source);

    @Named("toPossibleAnswerTexts")
    default List<String> toPossibleAnswerTexts(List<Answer> possibleAnswers) {
        return possibleAnswers.stream().map(Answer::text).toList();
    }

    @Named("toPossibleAnswers")
    default List<Answer> toPossibleAnswers(List<String> possibleAnswers) {
        return possibleAnswers.stream().map(Answer::new).toList();
    }
}
