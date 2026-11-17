package com.trivia.challenge.infrastructure.triviadatabase.config;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("trivia-challenge.triviadatabase")
public record OpenTriviaDbProperties(
        @DefaultValue("https://opentdb.com") URI baseUrl,
        @DefaultValue("15s") Duration connectTimeout,
        @DefaultValue("15s") Duration readTimeout) {

    public OpenTriviaDbProperties {
        Objects.requireNonNull(baseUrl, "baseUrl must not be null");
        Objects.requireNonNull(connectTimeout, "connectTimeout must not be null");
        Objects.requireNonNull(readTimeout, "readTimeout must not be null");
    }
}
