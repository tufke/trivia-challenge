package com.trivia.challenge.domain.model;

import com.trivia.challenge.domain.exception.ServiceException;
import java.util.Objects;

public record Category(String name) {

    public Category {
        Objects.requireNonNull(name, "name must not be null");
        if (name.isBlank()) {
            throw new ServiceException("Category name must not be blank");
        }
    }
}
