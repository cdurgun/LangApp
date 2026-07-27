package com.langapp.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /** Kullanicinin pratik yapmak istedigi hedef dil kodu: "en" ya da "ru" */
    @Column(name = "target_language", length = 5)
    private String targetLanguage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // --- Streak takibi ---
    @Column(name = "current_streak", nullable = false)
    private int currentStreak = 0;

    @Column(name = "longest_streak", nullable = false)
    private int longestStreak = 0;

    @Column(name = "last_active_date")
    private LocalDate lastActiveDate;

    @Column(name = "is_admin", nullable = false)
    private boolean admin = false;

    public User(String username, String email, String passwordHash, String targetLanguage) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.targetLanguage = targetLanguage;
    }
}
