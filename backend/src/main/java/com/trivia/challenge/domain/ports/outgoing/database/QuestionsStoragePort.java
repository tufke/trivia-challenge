package com.trivia.challenge.domain.ports.outgoing.database;

import com.trivia.challenge.domain.model.Question;
import java.util.List;

public interface QuestionsStoragePort {

    List<Question> getRandomQuestions(int amount);

    int getRemainingCapacity();

    void storeQuestions(List<Question> questions);
}
