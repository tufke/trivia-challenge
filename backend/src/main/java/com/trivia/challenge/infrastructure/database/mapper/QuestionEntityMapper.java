package com.trivia.challenge.infrastructure.database.mapper;

import com.trivia.challenge.domain.model.Answer;
import com.trivia.challenge.domain.model.Category;
import com.trivia.challenge.domain.model.Difficulty;
import com.trivia.challenge.domain.model.Question;
import com.trivia.challenge.domain.model.QuestionId;
import com.trivia.challenge.domain.model.QuestionType;
import com.trivia.challenge.infrastructure.database.model.QuestionEntity;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface QuestionEntityMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "toUuid")
    @Mapping(target = "category", source = "category", qualifiedByName = "toCategoryName")
    @Mapping(target = "type", source = "type", qualifiedByName = "toTypeValue")
    @Mapping(target = "difficulty", source = "difficulty", qualifiedByName = "toDifficultyValue")
    @Mapping(target = "question", source = "text")
    @Mapping(target = "correctAnswer", source = "correctAnswer", qualifiedByName = "toAnswerText")
    @Mapping(target = "incorrectAnswers", source = "incorrectAnswers", qualifiedByName = "toAnswerTexts")
    QuestionEntity toQuestionEntity(Question source);

    List<QuestionEntity> toQuestionEntities(List<Question> source);

    @Mapping(target = "id", source = "id", qualifiedByName = "toQuestionId")
    @Mapping(target = "category", source = "category", qualifiedByName = "toCategory")
    @Mapping(target = "type", source = "type", qualifiedByName = "toQuestionType")
    @Mapping(target = "difficulty", source = "difficulty", qualifiedByName = "toDifficulty")
    @Mapping(target = "text", source = "question")
    @Mapping(target = "correctAnswer", source = "correctAnswer", qualifiedByName = "toAnswer")
    @Mapping(target = "incorrectAnswers", source = "incorrectAnswers", qualifiedByName = "toAnswers")
    Question toQuestion(QuestionEntity source);

    List<Question> toQuestions(List<QuestionEntity> source);

    @Named("toUuid")
    default UUID toUuid(QuestionId questionId) {
        return questionId.value();
    }

    @Named("toQuestionId")
    default QuestionId toQuestionId(UUID uuid) {
        return new QuestionId(uuid);
    }

    @Named("toCategoryName")
    default String toCategoryName(Category category) {
        return category.name();
    }

    @Named("toCategory")
    default Category toCategory(String value) {
        return new Category(value);
    }

    @Named("toTypeValue")
    default String toTypeValue(QuestionType type) {
        return type.value();
    }

    @Named("toQuestionType")
    default QuestionType toQuestionType(String value) {
        return QuestionType.fromValue(value);
    }

    @Named("toDifficultyValue")
    default String toDifficultyValue(Difficulty difficulty) {
        return difficulty.value();
    }

    @Named("toDifficulty")
    default Difficulty toDifficulty(String value) {
        return Difficulty.fromValue(value);
    }

    @Named("toAnswerText")
    default String toAnswerText(Answer answer) {
        return answer.text();
    }

    @Named("toAnswer")
    default Answer toAnswer(String value) {
        return new Answer(value);
    }

    @Named("toAnswerTexts")
    default List<String> toAnswerTexts(List<Answer> answers) {
        return answers.stream().map(Answer::text).toList();
    }

    @Named("toAnswers")
    default List<Answer> toAnswers(List<String> values) {
        return values.stream().map(Answer::new).toList();
    }
}
