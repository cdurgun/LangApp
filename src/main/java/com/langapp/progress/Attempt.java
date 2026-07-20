package com.langapp.progress;

import com.langapp.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "attempts")
@Getter
@Setter
@NoArgsConstructor
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_type", nullable = false, length = 20)
    private ExerciseType exerciseType;

    /** Ilgili exercise tablosundaki id (quiz_questions, translation_exercises, vocab_items) */
    @Column(name = "exercise_id", nullable = false)
    private Long exerciseId;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "user_answer")
    private String userAnswer;

    @Column(name = "answered_at", nullable = false)
    private LocalDateTime answeredAt = LocalDateTime.now();

    public Attempt(User user, ExerciseType exerciseType, Long exerciseId, boolean correct, String userAnswer) {
        this.user = user;
        this.exerciseType = exerciseType;
        this.exerciseId = exerciseId;
        this.correct = correct;
        this.userAnswer = userAnswer;
    }
}
