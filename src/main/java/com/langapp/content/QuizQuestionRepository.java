package com.langapp.content;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByTopicId(Long topicId);
    List<QuizQuestion> findByTopicLanguageCode(String languageCode);
}
