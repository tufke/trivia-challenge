package com.trivia.challenge.infrastructure.database.cache;

import com.trivia.challenge.domain.exception.ServiceException;
import com.trivia.challenge.infrastructure.database.config.TriviaQuestionCacheProperties;
import com.trivia.challenge.infrastructure.database.model.QuestionEntity;
import com.trivia.challenge.infrastructure.database.model.RefreshState;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InMemoryQuestionCache {

    private final TriviaQuestionCacheProperties properties;

    private final AtomicBoolean bootstrapComplete = new AtomicBoolean(false);
    private final AtomicReference<Map<UUID, QuestionEntity>> activeQuestions = new AtomicReference<>(Map.of());

    private final AtomicBoolean refreshCompleted = new AtomicBoolean(false);
    private final AtomicReference<Map<UUID, QuestionEntity>> refreshQuestions = new AtomicReference<>(Map.of());
    private final AtomicBoolean allAvailableQuestionsRetrieved = new AtomicBoolean(false);

    private final AtomicReference<Instant> lastRefreshAttempt = new AtomicReference<>(Instant.now());
    private final AtomicBoolean refreshCycleStarted = new AtomicBoolean(false);

    public void addQuestions(List<QuestionEntity> additions) {
        addToPool(activeQuestions, additions);
    }

    public void addRefreshQuestions(List<QuestionEntity> additions) {
        addToPool(refreshQuestions, additions);
    }

    private void addToPool(AtomicReference<Map<UUID, QuestionEntity>> pool, List<QuestionEntity> additions) {

        var entitiesToAdd = List.copyOf(additions);
        if (entitiesToAdd.isEmpty()) {
            return;
        }

        pool.updateAndGet(snapshot -> {
            var mutableSnapshot = new HashMap<>(snapshot);
            for (var entity : entitiesToAdd) {
                if (mutableSnapshot.size() >= properties.targetSize()) {
                    break;
                }

                mutableSnapshot.putIfAbsent(entity.id(), entity);
            }

            return Map.copyOf(mutableSnapshot);
        });
    }

    public int activeSize() {
        return activeQuestions.get().size();
    }

    public int remainingActiveCapacity() {
        return Math.max(0, properties.targetSize() - activeSize());
    }

    public int refreshSize() {
        return refreshQuestions.get().size();
    }

    public int remainingRefreshCapacity() {
        return Math.max(0, properties.targetSize() - refreshSize());
    }

    public void markBootstrapComplete() {
        bootstrapComplete.set(true);
    }

    public boolean isBootstrapComplete() {
        return bootstrapComplete.get();
    }

    public void markRefreshCompleted() {
        refreshCompleted.set(true);
    }

    public void markAllAvailableQuestionsRetrieved() {
        allAvailableQuestionsRetrieved.set(true);
    }

    public boolean allAvailableQuestionsRetrieved() {
        return allAvailableQuestionsRetrieved.get();
    }

    public boolean canPromoteRefreshPool() {
        return refreshCompleted.get() && refreshQuestions.get().size() >= properties.minimumSize();
    }

    public RefreshState getRefreshState() {
        if (canPromoteRefreshPool() || allAvailableQuestionsRetrieved.get()) {
            return RefreshState.COMPLETED;
        }
        return refreshCycleStarted.get() ? RefreshState.BUILDING : RefreshState.IDLE;
    }

    public List<QuestionEntity> getRandomQuestions(int amount) {
        if (amount <= 0) {
            throw new ServiceException("Question amount must be positive but was " + amount);
        }
        var snapshot = activeQuestions.get();
        if (snapshot.size() < amount) {
            throw new ServiceException(
                    "Requested %d questions but only %d are cached".formatted(amount, snapshot.size()));
        }
        var selected = new ArrayList<>(snapshot.values());
        Collections.shuffle(selected);
        return List.copyOf(selected.subList(0, amount));
    }

    public boolean promoteRefreshPool() {
        if (!canPromoteRefreshPool()) {
            return false;
        }

        activeQuestions.set(refreshQuestions.get());
        refreshQuestions.set(Map.of());

        bootstrapComplete.set(true);
        refreshCompleted.set(false);
        allAvailableQuestionsRetrieved.set(false);
        refreshCycleStarted.set(false);

        return true;
    }

    public boolean isRefreshCycleStarted() {
        return refreshCycleStarted.get();
    }

    public boolean refreshRequired() {
        return Instant.now().isAfter(lastRefreshAttempt.get().plus(properties.refreshInterval()));
    }

    public void startRefreshCycle() {
        refreshQuestions.set(Map.of());
        refreshCompleted.set(false);
        allAvailableQuestionsRetrieved.set(false);
        lastRefreshAttempt.set(Instant.now());
        refreshCycleStarted.set(true);
    }

    public void stopRefreshCycle() {
        refreshCycleStarted.set(false);
    }
}
