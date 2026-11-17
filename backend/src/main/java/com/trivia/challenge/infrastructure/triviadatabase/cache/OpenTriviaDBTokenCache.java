package com.trivia.challenge.infrastructure.triviadatabase.cache;

import java.util.concurrent.atomic.AtomicReference;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class OpenTriviaDBTokenCache {

    private final AtomicReference<@Nullable String> token = new AtomicReference<>();

    public @Nullable String getToken() {
        return token.get();
    }

    public void setToken(String token) {
        this.token.set(token);
    }

    public void clear() {
        token.set(null);
    }
}
