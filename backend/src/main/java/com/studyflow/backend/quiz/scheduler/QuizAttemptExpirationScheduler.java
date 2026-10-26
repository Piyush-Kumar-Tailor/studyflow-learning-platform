package com.studyflow.backend.quiz.scheduler;

import com.studyflow.backend.quiz.entity.QuizAttempt;
import com.studyflow.backend.quiz.enums.QuizAttemptStatus;
import com.studyflow.backend.quiz.repository.QuizAttemptRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class QuizAttemptExpirationScheduler {

    private final QuizAttemptRepository attemptRepository;

    public QuizAttemptExpirationScheduler(
            QuizAttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    /**
     * Runs every 60 seconds and expires unfinished quiz attempts
     * whose configured duration has elapsed.
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void expireAbandonedAttempts() {

        LocalDateTime now = LocalDateTime.now();

        List<QuizAttempt> activeAttempts =
                attemptRepository.findByStatus(
                        QuizAttemptStatus.IN_PROGRESS
                );

        int expiredCount = 0;

        for (QuizAttempt attempt : activeAttempts) {

            if (attempt.getStartedAt() == null
                    || attempt.getQuiz() == null
                    || attempt.getQuiz().getDurationMinutes() == null) {
                continue;
            }

            LocalDateTime expiresAt = attempt.getStartedAt()
                    .plusMinutes(
                            attempt.getQuiz().getDurationMinutes()
                    );

            if (!now.isBefore(expiresAt)) {
                attempt.setStatus(QuizAttemptStatus.EXPIRED);
                expiredCount++;
            }
        }

        if (expiredCount > 0) {
            attemptRepository.saveAll(activeAttempts.stream()
                    .filter(attempt ->
                            attempt.getStatus()
                                    == QuizAttemptStatus.EXPIRED)
                    .toList());
        }
    }
}