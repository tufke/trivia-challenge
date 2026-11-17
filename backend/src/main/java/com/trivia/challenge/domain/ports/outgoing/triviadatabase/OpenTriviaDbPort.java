package com.trivia.challenge.domain.ports.outgoing.triviadatabase;

import com.trivia.challenge.domain.model.Question;
import java.util.List;

public interface OpenTriviaDbPort {

    void prepareFreshSession();

    Result getQuestions(int amount);

    sealed interface Result permits Result.Success, Result.PoolExhausted, Result.NothingRetrieved {

        record Success(List<Question> questions) implements Result {}

        record PoolExhausted() implements Result {}

        record NothingRetrieved() implements Result {}
    }
}
