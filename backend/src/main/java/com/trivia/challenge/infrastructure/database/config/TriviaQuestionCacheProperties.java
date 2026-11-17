package com.trivia.challenge.infrastructure.database.config;

import java.time.Duration;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("trivia-challenge.database.question-cache")
public record TriviaQuestionCacheProperties(
        @DefaultValue("5000") int targetSize,
        @DefaultValue("1000") int minimumSize,
        @DefaultValue("PT6H") Duration refreshInterval) {

    public TriviaQuestionCacheProperties {
        if (targetSize <= 0) {
            throw new IllegalArgumentException("targetSize must be greater than 0");
        }

        if (minimumSize <= 0) {
            throw new IllegalArgumentException("minimumSize must be greater than 0");
        }

        if (minimumSize > targetSize) {
            throw new IllegalArgumentException("minimumSize must not be greater than targetSize");
        }

        Objects.requireNonNull(refreshInterval, "refreshInterval must not be null");
    }
}
