package com.trivia.challenge.domain.model;

import com.trivia.challenge.domain.exception.ServiceException;
import java.util.Arrays;
import java.util.Objects;

public enum Difficulty {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    private final String value;

    Difficulty(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static Difficulty fromValue(String value) {
        Objects.requireNonNull(value, "value must not be null");
        return Arrays.stream(values())
                .filter(difficulty -> difficulty.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ServiceException("Unknown difficulty: " + value));
    }
}
