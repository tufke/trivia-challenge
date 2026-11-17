package com.trivia.challenge.infrastructure.triviadatabase.mapper;

import com.trivia.challenge.domain.model.Answer;
import com.trivia.challenge.domain.model.Category;
import com.trivia.challenge.domain.model.Difficulty;
import com.trivia.challenge.domain.model.Question;
import com.trivia.challenge.domain.model.QuestionId;
import com.trivia.challenge.domain.model.QuestionType;
import com.trivia.challenge.infrastructure.triviadatabase.rest.client.model.QuestionDto;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface QuestionMapper {

    @Mapping(target = "id", source = ".", qualifiedByName = "toQuestionId")
    @Mapping(target = "category", source = "category", qualifiedByName = "toCategory")
    @Mapping(target = "type", source = "type", qualifiedByName = "toQuestionType")
    @Mapping(target = "difficulty", source = "difficulty", qualifiedByName = "toDifficulty")
    @Mapping(target = "text", source = "question", qualifiedByName = "decode")
    @Mapping(target = "correctAnswer", source = "correctAnswer", qualifiedByName = "toAnswer")
    @Mapping(target = "incorrectAnswers", source = "incorrectAnswers", qualifiedByName = "toAnswers")
    Question toQuestion(QuestionDto source);

    @Named("toQuestionId")
    default QuestionId toQuestionId(QuestionDto ignored) {
        return QuestionId.generate();
    }

    @Named("toCategory")
    default Category toCategory(String value) {
        return new Category(decode(value));
    }

    @Named("toQuestionType")
    default QuestionType toQuestionType(String value) {
        return QuestionType.valueOf(decode(value).toUpperCase());
    }

    @Named("toDifficulty")
    default Difficulty toDifficulty(String value) {
        return Difficulty.valueOf(decode(value).toUpperCase());
    }

    @Named("toAnswer")
    default Answer toAnswer(String value) {
        return new Answer(decode(value));
    }

    @Named("toAnswers")
    default List<Answer> toAnswers(List<String> values) {
        return values.stream().map(this::toAnswer).toList();
    }

    @Named("decode")
    default String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
