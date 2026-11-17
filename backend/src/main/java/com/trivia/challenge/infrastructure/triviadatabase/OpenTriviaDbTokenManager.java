package com.trivia.challenge.infrastructure.triviadatabase;

import com.trivia.challenge.domain.exception.ServiceException;
import com.trivia.challenge.infrastructure.triviadatabase.cache.OpenTriviaDBTokenCache;
import com.trivia.challenge.infrastructure.triviadatabase.rest.client.api.OpenTriviaDbApi;
import com.trivia.challenge.infrastructure.triviadatabase.rest.client.model.ResponseCodeDto;
import com.trivia.challenge.infrastructure.triviadatabase.rest.client.model.SessionTokenDto;
import com.trivia.challenge.infrastructure.triviadatabase.rest.client.model.TokenCommandDto;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class OpenTriviaDbTokenManager {

    private final OpenTriviaDbApi openTriviaDbApi;
    private final OpenTriviaDBTokenCache tokenCache;

    public void prepareFreshSession() {
        var token = tokenCache.getToken();
        if (token == null) {
            requestToken();
        } else {
            resetToken(token);
        }
    }

    public String getOrCreateToken() {
        var token = tokenCache.getToken();
        return token == null ? requestToken() : token;
    }

    public String requestToken() {
        return executeTokenRequest(TokenCommandDto.REQUEST, null, "Failed to request Open Trivia DB session token");
    }

    public void resetToken(String token) {
        executeTokenRequest(TokenCommandDto.RESET, token, "Failed to reset Open Trivia DB session token");
    }

    private String executeTokenRequest(TokenCommandDto command, @Nullable String token, String failureMessage) {
        SessionTokenDto response;
        try {
            response = required(openTriviaDbApi.manageToken(command, token), "session token response");
        } catch (RestClientException exception) {
            throw new ServiceException(failureMessage, exception);
        }
        validateResponseCode(required(response.getResponseCode(), "session token response code"), failureMessage);
        var newToken = required(response.getToken(), "session token");
        if (newToken.isBlank()) {
            throw new ServiceException(failureMessage + ": returned token is blank");
        }
        tokenCache.setToken(newToken);
        return newToken;
    }

    private static void validateResponseCode(ResponseCodeDto code, String failureMessage) {
        switch (code) {
            case SUCCESS -> {
                /* do nothing, everything is fine */
            }
            case NO_RESULTS -> throw new ServiceException(failureMessage + ": no questions available");
            case INVALID_PARAMETER -> throw new ServiceException(failureMessage + ": invalid parameter");
            case TOKEN_NOT_FOUND -> throw new ServiceException(failureMessage + ": session token not found");
            case TOKEN_EMPTY -> throw new ServiceException(failureMessage + ": session token exhausted");
        }
    }

    private static <T> T required(@Nullable T value, String field) {
        if (value == null) {
            throw new ServiceException("Invalid Open Trivia DB response: missing " + field);
        }
        return value;
    }
}
