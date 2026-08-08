package com.langapp.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vocab_items")
@Getter
@Setter
@NoArgsConstructor
public class VocabItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    /** Hedef dildeki kelime/ifade, orn. "apple" */
    @Column(name = "source_text", nullable = false)
    private String sourceText;

    /** Ana dildeki karsiligi, orn. "elma" */
    @Column(name = "target_text", nullable = false)
    private String targetText;

    @Column(name = "example_sentence")
    private String exampleSentence;

    /** Kelimenin turu: isim, fiil, sifat vb. Mevcut kayitlarda null olabilir (geriye donuk uyumluluk). */
    @Enumerated(EnumType.STRING)
    @Column(name = "word_type", length = 20)
    private WordType wordType;

    /** Sadece FIIL (VERB) kelimeler icin anlamli: bitmis mi bitmemis mi. */
    @Enumerated(EnumType.STRING)
    @Column(name = "aspect", length = 20)
    private VerbAspect aspect;

    /**
     * Bu fiilin diger gorunuse (aspect) sahip esi, orn. govorit' (bitmemis) <-> skazat' (bitmis).
     * Kendine referans veren nullable iliski; karsilikli olarak iki tarafta da doldurulmasi beklenir.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aspect_pair_id")
    private VocabItem aspectPair;

    /**
     * Opsiyonel, elle girilmis ses dosyasi linki. Doluysa on yuzde tarayici TTS
     * yerine bu dosya calinir - ozellikle vurgusu kritik kelimeler icin kaliteli,
     * dogrulanmis telaffuz saglamak amaciyla.
     */
    @Column(name = "audio_url")
    private String audioUrl;
}
