package com.trivia.challenge.domain.ports.outgoing.database;

public interface QuestionsLifecyclePort {

    LifecyclePhase getLifecyclePhase();

    boolean isRefreshRequired();

    void startRefreshCycle();

    void complete();

    void completeAllAvailableQuestionsRetrieved();

    boolean canActivateCompletedRefresh();

    boolean activateCompletedRefresh();
}
