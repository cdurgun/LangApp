package com.langapp.progress;

import com.langapp.content.Topic;
import com.langapp.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "topic_id"}))
@Getter
@Setter
@NoArgsConstructor
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    /** 0-100 arasi basari seviyesi */
    @Column(name = "mastery_level", nullable = false)
    private int masteryLevel = 0;

    @Column(name = "last_practiced_at")
    private LocalDateTime lastPracticedAt;

    public UserProgress(User user, Topic topic) {
        this.user = user;
        this.topic = topic;
    }

    /**
     * Yeni bir denemenin sonucuna gore mastery seviyesini gunceller.
     * Basit hareketli ortalama: dogruysa +5, yanlissa -3, 0-100 araliginda sabitlenir.
     */
    public void applyAttemptResult(boolean correct) {
        int delta = correct ? 5 : -3;
        this.masteryLevel = Math.max(0, Math.min(100, this.masteryLevel + delta));
        this.lastPracticedAt = LocalDateTime.now();
    }
}
