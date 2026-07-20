package com.langapp.content;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TranslationExerciseRepository extends JpaRepository<TranslationExercise, Long> {
    List<TranslationExercise> findByTopicId(Long topicId);
    List<TranslationExercise> findByTopicLanguageCode(String languageCode);
}
