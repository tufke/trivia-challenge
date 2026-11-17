package com.trivia.challenge.infrastructure.database;

import com.trivia.challenge.domain.exception.ServiceException;
import com.trivia.challenge.domain.model.Question;
import com.trivia.challenge.domain.ports.outgoing.database.LifecyclePhase;
import com.trivia.challenge.domain.ports.outgoing.database.QuestionsLifecyclePort;
import com.trivia.challenge.domain.ports.outgoing.database.QuestionsStoragePort;
import com.trivia.challenge.infrastructure.database.cache.InMemoryQuestionCache;
import com.trivia.challenge.infrastructure.database.mapper.QuestionEntityMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuestionsStorageAdapter implements QuestionsStoragePort, QuestionsLifecyclePort {

    private final InMemoryQuestionCache inMemoryQuestionCache;
    private final QuestionEntityMapper questionEntityMapper;

    @Override
    public int getRemainingCapacity() {
        return routesToActiveQuestions()
                ? inMemoryQuestionCache.remainingActiveCapacity()
                : inMemoryQuestionCache.remainingRefreshCapacity();
    }

    @Override
    public List<Question> getRandomQuestions(int amount) {
        return questionEntityMapper.toQuestions(inMemoryQuestionCache.getRandomQuestions(amount));
    }

    @Override
    public void storeQuestions(List<Question> questions) {
        var active = routesToActiveQuestions();
        var entities = questionEntityMapper.toQuestionEntities(questions);
        if (active) {
            inMemoryQuestionCache.addQuestions(entities);
        } else {
            inMemoryQuestionCache.addRefreshQuestions(entities);
        }
    }

    private boolean routesToActiveQuestions() {
        var phase = getLifecyclePhase();
        return switch (phase) {
            case BOOTSTRAPPING -> true;
            case REFRESHING -> false;
            case IDLE, COMPLETED ->
                throw new ServiceException(
                        "Cannot access maintenance question storage while maintenance phase is " + phase);
        };
    }

    @Override
    public LifecyclePhase getLifecyclePhase() {
        if (!inMemoryQuestionCache.isBootstrapComplete()) {
            return LifecyclePhase.BOOTSTRAPPING;
        }
        return switch (inMemoryQuestionCache.getRefreshState()) {
            case IDLE -> LifecyclePhase.IDLE;
            case BUILDING -> LifecyclePhase.REFRESHING;
            case COMPLETED -> LifecyclePhase.COMPLETED;
        };
    }

    @Override
    public void complete() {
        var phase = getLifecyclePhase();
        switch (phase) {
            case BOOTSTRAPPING -> {
                inMemoryQuestionCache.markBootstrapComplete();
                log.info("Bootstrap completed at target");
            }
            case REFRESHING -> {
                inMemoryQuestionCache.markRefreshCompleted();
                log.info("Refresh completed at target");
            }
            case IDLE, COMPLETED ->
                throw new ServiceException("Cannot complete maintenance at target while maintenance phase is " + phase);
        }
    }

    @Override
    public void completeAllAvailableQuestionsRetrieved() {
        var phase = getLifecyclePhase();
        switch (phase) {
            case BOOTSTRAPPING -> {
                inMemoryQuestionCache.markBootstrapComplete();
                log.info("Bootstrap completed after retrieval exhaustion");
            }
            case REFRESHING -> {
                inMemoryQuestionCache.markAllAvailableQuestionsRetrieved();
                inMemoryQuestionCache.stopRefreshCycle();
                inMemoryQuestionCache.markRefreshCompleted();
                log.info("Refresh completed after retrieval exhaustion");
            }
            case IDLE, COMPLETED ->
                throw new ServiceException(
                        "Cannot complete lifecycle after source exhaustion while lifecycle phase is " + phase);
        }
    }

    @Override
    public boolean isRefreshRequired() {
        return inMemoryQuestionCache.refreshRequired();
    }

    @Override
    public boolean canActivateCompletedRefresh() {
        return inMemoryQuestionCache.canPromoteRefreshPool();
    }

    @Override
    public boolean activateCompletedRefresh() {
        return inMemoryQuestionCache.promoteRefreshPool();
    }

    @Override
    public void startRefreshCycle() {
        inMemoryQuestionCache.startRefreshCycle();
    }
}
