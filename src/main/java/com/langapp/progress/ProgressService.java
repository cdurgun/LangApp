package com.langapp.progress;

import com.langapp.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgressService {

    private final UserProgressRepository userProgressRepository;
    private final AttemptRepository attemptRepository;

    public ProgressService(UserProgressRepository userProgressRepository, AttemptRepository attemptRepository) {
        this.userProgressRepository = userProgressRepository;
        this.attemptRepository = attemptRepository;
    }

    public List<UserProgress> getProgressForUser(User user) {
        return userProgressRepository.findByUserIdWithTopic(user.getId());
    }

    public int getOverallAccuracyPercent(User user) {
        long total = attemptRepository.countByUserId(user.getId());
        if (total == 0) return 0;
        long correct = attemptRepository.countByUserIdAndCorrectTrue(user.getId());
        return (int) Math.round((correct * 100.0) / total);
    }

    public List<Attempt> getRecentAttempts(User user) {
        return attemptRepository.findByUserIdOrderByAnsweredAtDesc(user.getId());
    }
}
