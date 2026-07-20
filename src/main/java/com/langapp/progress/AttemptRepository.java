package com.langapp.progress;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    List<Attempt> findByUserIdOrderByAnsweredAtDesc(Long userId);
    long countByUserIdAndCorrectTrue(Long userId);
    long countByUserId(Long userId);
}
