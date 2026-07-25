package com.langapp.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Bir fiile (VocabItem, word_type = VERB) ait cekim tablosu.
 * Rusca fiil cekimi 6 sahis (ben/sen/o/biz/siz/onlar) uzerinden gider;
 * simdiki ve gelecek zaman icin 6'sar, emir kipi icin 2 (sen/siz) alan var.
 * Ingilizce gibi daha az cekimli dillerde ilgisiz alanlar bos birakilabilir.
 */
@Entity
@Table(name = "verb_conjugations")
@Getter
@Setter
@NoArgsConstructor
public class VerbConjugation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Fiil kelimesine 1-1 baglanti; ayni kelime icin birden fazla cekim kaydi olmamali. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocab_item_id", nullable = false, unique = true)
    private VocabItem vocabItem;

    // --- Şimdiki zaman (6 şahıs) ---
    @Column(name = "present_1s")
    private String present1s; // ben

    @Column(name = "present_2s")
    private String present2s; // sen

    @Column(name = "present_3s")
    private String present3s; // o

    @Column(name = "present_1p")
    private String present1p; // biz

    @Column(name = "present_2p")
    private String present2p; // siz

    @Column(name = "present_3p")
    private String present3p; // onlar

    // --- Gelecek zaman (6 şahıs) ---
    @Column(name = "future_1s")
    private String future1s;

    @Column(name = "future_2s")
    private String future2s;

    @Column(name = "future_3s")
    private String future3s;

    @Column(name = "future_1p")
    private String future1p;

    @Column(name = "future_2p")
    private String future2p;

    @Column(name = "future_3p")
    private String future3p;

    // --- Emir kipi (sen / siz) ---
    @Column(name = "imperative_singular")
    private String imperativeSingular; // sen

    @Column(name = "imperative_plural")
    private String imperativePlural; // siz

    public VerbConjugation(VocabItem vocabItem) {
        this.vocabItem = vocabItem;
    }
}
