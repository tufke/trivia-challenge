package com.trivia.challenge.infrastructure.triviadatabase;

import com.trivia.challenge.domain.exception.ServiceException;
import com.trivia.challenge.domain.ports.outgoing.triviadatabase.OpenTriviaDbPort;
import com.trivia.challenge.infrastructure.triviadatabase.mapper.QuestionMapper;
import com.trivia.challenge.infrastructure.triviadatabase.rest.client.api.OpenTriviaDbApi;
import com.trivia.challenge.infrastructure.triviadatabase.rest.client.model.EncodingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OpenTriviaDbAdapter implements OpenTriviaDbPort {

    private static final int MAX_QUESTIONS_PER_REQUEST = 50;

    private final OpenTriviaDbApi openTriviaDbApi;
    private final OpenTriviaDbTokenManager openTriviaDbTokenManager;
    private final QuestionMapper questionMapper;

    @Override
    public void prepareFreshSession() {
        openTriviaDbTokenManager.prepareFreshSession();
    }

    @Override
    public Result getQuestions(int amount) {
        if (amount < 1 || amount > MAX_QUESTIONS_PER_REQUEST) {
            throw new ServiceException("OpenTriviaDB supports requesting between 1 and 50 questions");
        }

        try {
            var token = openTriviaDbTokenManager.getOrCreateToken();
            var response = openTriviaDbApi.getQuestions(amount, null, null, null, token, EncodingDto.BASE64);

            switch (response.getResponseCode()) {
                case SUCCESS:
                    return new Result.Success(response.getResults().stream()
                            .map(questionMapper::toQuestion)
                            .toList());

                case TOKEN_NOT_FOUND:
                    log.info("OpenTriviaDB token {} not found, requesting a new token", token);
                    openTriviaDbTokenManager.requestToken();
                    return new Result.NothingRetrieved();

                case TOKEN_EMPTY:
                    log.info("OpenTriviaDB token {} is empty, resetting token", token);
                    openTriviaDbTokenManager.prepareFreshSession();
                    log.info("OpenTriviaDB session exhausted, resetting the token");
                    return new Result.PoolExhausted();

                case NO_RESULTS:
                    log.info("OpenTriviaDB returned no results for request of {} questions", amount);
                    return new Result.NothingRetrieved();

                case INVALID_PARAMETER:
                    throw new ServiceException("Invalid OpenTriviaDB parameters");

                default:
                    throw new ServiceException("Unsupported response code: " + response.getResponseCode());
            }

        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("OpenTriviaDB rate limit exceeded", e);
            return new Result.NothingRetrieved();
        }
    }
}
