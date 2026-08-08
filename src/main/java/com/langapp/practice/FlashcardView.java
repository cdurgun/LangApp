package com.langapp.practice;

/**
 * VocabItem entity'sini dogrudan JSON'a serialize etmek yerine kullanilan DTO.
 * Boylece Jackson, entity uzerindeki LAZY iliskilere (topic, topic.language)
 * dokunup Hibernate LazyInitializationException firlatmiyor.
 */
public record FlashcardView(Long id, String sourceText, String targetText, String audioUrl) {
}
