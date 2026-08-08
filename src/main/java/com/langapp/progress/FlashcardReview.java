package com.langapp.progress;

import com.langapp.content.VocabItem;
import com.langapp.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Bir kullanicinin bir flashcard kelimesi icin SM-2 tabanli tekrar durumu.
 * Klasik SM-2 algoritmasinin sadelestirilmis hali: kalite puani yerine
 * "biliyordum/bilmiyordum" ikili sinyali kullaniliyor (5 / 2 olarak eslenir).
 */
@Entity
@Table(name = "flashcard_reviews", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "vocab_item_id"}))
@Getter
@Setter
@NoArgsConstructor
public class FlashcardReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocab_item_id", nullable = false)
    private VocabItem vocabItem;

    @Column(name = "ease_factor", nullable = false)
    private double easeFactor = 2.5;

    @Column(name = "interval_days", nullable = false)
    private int intervalDays = 0;

    @Column(name = "repetitions", nullable = false)
    private int repetitions = 0;

    @Column(name = "next_review_date", nullable = false)
    private LocalDate nextReviewDate = LocalDate.now();

    @Column(name = "last_reviewed_at")
    private LocalDateTime lastReviewedAt;

    public FlashcardReview(User user, VocabItem vocabItem) {
        this.user = user;
        this.vocabItem = vocabItem;
    }

    /**
     * SM-2 guncellemesi. Kalite puani yerine ikili sinyal kullanildigi icin
     * "biliyordum" = 5 (mukemmel), "bilmiyordum" = 2 (yetersiz, tekrar 0'a duser).
     */
    public void applyReview(boolean knewIt) {
        int quality = knewIt ? 5 : 2;

        if (quality < 3) {
            repetitions = 0;
            intervalDays = 1;
        } else {
            if (repetitions == 0) {
                intervalDays = 1;
            } else if (repetitions == 1) {
                intervalDays = 6;
            } else {
                intervalDays = (int) Math.round(intervalDays * easeFactor);
            }
            repetitions++;
        }

        double newEase = easeFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
        easeFactor = Math.max(1.3, newEase);

        nextReviewDate = LocalDate.now().plusDays(intervalDays);
        lastReviewedAt = LocalDateTime.now();
    }
}
