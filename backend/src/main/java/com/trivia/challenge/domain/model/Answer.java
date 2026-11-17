package com.trivia.challenge.domain.model;

import java.util.Objects;

public record Answer(String text) {

    public Answer {
        Objects.requireNonNull(text, "text must not be null");
    }

    public boolean matches(Answer other) {
        return text.strip().equalsIgnoreCase(other.text.strip());
    }
}
