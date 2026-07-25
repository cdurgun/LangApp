package com.langapp.content;

public enum WordType {
    NOUN,          // isim
    VERB,          // fiil
    ADJECTIVE,     // sıfat
    ADVERB,        // zarf
    PRONOUN,       // zamir
    PREPOSITION,   // edat/ilgeç
    CONJUNCTION,   // bağlaç
    NUMERAL,       // sayı
    OTHER;         // diğer

    public String getTurkishLabel() {
        return switch (this) {
            case NOUN -> "İsim";
            case VERB -> "Fiil";
            case ADJECTIVE -> "Sıfat";
            case ADVERB -> "Zarf";
            case PRONOUN -> "Zamir";
            case PREPOSITION -> "Edat";
            case CONJUNCTION -> "Bağlaç";
            case NUMERAL -> "Sayı";
            case OTHER -> "Diğer";
        };
    }
}
