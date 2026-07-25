package com.langapp.content;

/** Rusça fiillerin görünüşü (aspect): bitmemiş (sürekli/tekrarlanan) ya da bitmiş (tek seferlik/tamamlanan). */
public enum VerbAspect {
    IMPERFECTIVE, // bitmemiş
    PERFECTIVE;   // bitmiş

    public String getTurkishLabel() {
        return this == IMPERFECTIVE ? "Bitmemiş" : "Bitmiş";
    }
}
