package com.trivia.challenge.infrastructure.database.config;

import java.time.Duration;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("trivia-challenge.database.quiz-cache")
public record TriviaQuizCacheProperties(
        @DefaultValue("PT30M") Duration quizTtl) {

    public TriviaQuizCacheProperties {
        Objects.requireNonNull(quizTtl, "quizTtl must not be null");
        if (quizTtl.isNegative() || quizTtl.isZero()) {
            throw new IllegalArgumentException("quizTtl must be greater than 0");
        }
    }
}
