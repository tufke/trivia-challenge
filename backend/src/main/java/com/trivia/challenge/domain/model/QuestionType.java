package com.trivia.challenge.domain.model;

import com.trivia.challenge.domain.exception.ServiceException;
import java.util.Arrays;
import java.util.Objects;

public enum QuestionType {
    MULTIPLE("multiple"),
    BOOLEAN("boolean");

    private final String value;

    QuestionType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static QuestionType fromValue(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return Arrays.stream(values())
                .filter(type -> type.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ServiceException("Unknown question type: " + value));
    }
}
