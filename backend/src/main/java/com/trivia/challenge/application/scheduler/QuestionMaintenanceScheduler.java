package com.trivia.challenge.application.scheduler;

import com.trivia.challenge.domain.ports.incoming.MaintainQuestionsUseCase;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class QuestionMaintenanceScheduler {

    private final MaintainQuestionsUseCase maintainQuestionsUseCase;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Scheduled(fixedDelay = 10_000)
    public void maintainQuestions() {
        if (!running.compareAndSet(false, true)) {
            log.debug("Maintenance already running, skipping execution");
            return;
        }

        try {
            log.info("Starting question maintenance");
            maintainQuestionsUseCase.maintainQuestions();
            log.info("Finished question maintenance");
        } finally {
            running.set(false);
        }
    }
}
