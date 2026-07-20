package com.langapp.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "translation_exercises")
@Getter
@Setter
@NoArgsConstructor
public class TranslationExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    /** Kullaniciya gosterilen, cevrilecek metin */
    @Column(name = "source_text", nullable = false)
    private String sourceText;

    @Column(name = "source_lang", nullable = false, length = 5)
    private String sourceLang;

    @Column(name = "target_lang", nullable = false, length = 5)
    private String targetLang;

    /** Referans ceviri; kullanici cevabi buna karsi benzerlik skoruyla kontrol edilir */
    @Column(name = "expected_translation", nullable = false)
    private String expectedTranslation;

    @Column(name = "hint")
    private String hint;
}
