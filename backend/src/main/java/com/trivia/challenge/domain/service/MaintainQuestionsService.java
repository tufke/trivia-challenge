package com.trivia.challenge.domain.service;

import com.trivia.challenge.domain.ports.incoming.MaintainQuestionsUseCase;
import com.trivia.challenge.domain.ports.outgoing.database.QuestionsLifecyclePort;
import com.trivia.challenge.domain.ports.outgoing.database.QuestionsStoragePort;
import com.trivia.challenge.domain.ports.outgoing.triviadatabase.OpenTriviaDbPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MaintainQuestionsService implements MaintainQuestionsUseCase {

    private static final int BATCH_SIZE = 50;

    private final OpenTriviaDbPort openTriviaDbPort;
    private final QuestionsStoragePort questionsStoragePort;
    private final QuestionsLifecyclePort questionsLifecyclePort;

    @Override
    public void maintainQuestions() {
        switch (questionsLifecyclePort.getLifecyclePhase()) {
            case BOOTSTRAPPING, REFRESHING -> retrieveAndStoreQuestions();
            case IDLE -> startRefreshIfRequired();
            case COMPLETED -> activateCompletedRefresh();
        }
    }

    private void retrieveAndStoreQuestions() {
        log.info("Retrieving and storing questions");
        var remaining = questionsStoragePort.getRemainingCapacity();
        if (remaining == 0) {
            questionsLifecyclePort.complete();
            return;
        }

        var amount = Math.min(remaining, BATCH_SIZE);
        switch (openTriviaDbPort.getQuestions(amount)) {
            case OpenTriviaDbPort.Result.Success(var questions) -> {
                log.info("Retrieved {} questions from Open Trivia DB", questions.size());
                questionsStoragePort.storeQuestions(questions);
            }

            case OpenTriviaDbPort.Result.PoolExhausted _ -> {
                log.info("No more questions available from Open Trivia DB");
                questionsLifecyclePort.completeAllAvailableQuestionsRetrieved();
            }

            case OpenTriviaDbPort.Result.NothingRetrieved _ -> log.debug("No questions retrieved");
        }
    }

    private void startRefreshIfRequired() {
        log.info("Checking if refresh is required");
        if (!questionsLifecyclePort.isRefreshRequired()) {
            return;
        }
        openTriviaDbPort.prepareFreshSession();
        questionsLifecyclePort.startRefreshCycle();
        log.info("Starting refresh cycle");
        retrieveAndStoreQuestions();
    }

    private void activateCompletedRefresh() {
        log.info("Checking if completed refresh can be activated");
        if (questionsLifecyclePort.canActivateCompletedRefresh() && questionsLifecyclePort.activateCompletedRefresh()) {
            log.info("Completed refresh activated");
        }
    }
}
