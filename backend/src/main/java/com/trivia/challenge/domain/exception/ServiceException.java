package com.trivia.challenge.domain.exception;

import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        this(message, null);
    }

    public ServiceException(String message, @Nullable Throwable cause) {
        super(Objects.requireNonNull(message), cause);
    }
}
