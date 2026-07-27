package com.langapp.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByLanguageCodeOrderByLevelAsc(String languageCode);

    /** Admin kelime ekleme formundaki konu dropdown'u icin: dil bilgisi eager cekiliyor. */
    @Query("SELECT t FROM Topic t JOIN FETCH t.language ORDER BY t.language.code, t.level, t.name")
    List<Topic> findAllWithLanguage();
}
