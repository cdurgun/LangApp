package com.langapp.progress;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlashcardReviewRepository extends JpaRepository<FlashcardReview, Long> {
    Optional<FlashcardReview> findByUserIdAndVocabItemId(Long userId, Long vocabItemId);
}
