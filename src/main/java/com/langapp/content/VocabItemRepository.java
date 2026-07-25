package com.langapp.content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VocabItemRepository extends JpaRepository<VocabItem, Long> {
    List<VocabItem> findByTopicId(Long topicId);
    List<VocabItem> findByTopicLanguageCode(String languageCode);

    /**
     * Kelime listesi ekrani icin: dile gore filtreler, opsiyonel olarak
     * kelime/karsilik metninde arama yapar, sayfalanmis sonuc doner.
     * JOIN FETCH ile topic'i birlikte cekiyoruz (ManyToOne oldugu icin
     * sayfalamada satir cogalmasi olmaz) ki view'da LazyInitializationException
     * alinmasin.
     */
    @Query(value = "SELECT v FROM VocabItem v JOIN FETCH v.topic t JOIN FETCH t.language l " +
            "WHERE l.code = :languageCode " +
            "AND (:search = '' OR LOWER(v.sourceText) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "     OR LOWER(v.targetText) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT COUNT(v) FROM VocabItem v JOIN v.topic t JOIN t.language l " +
            "WHERE l.code = :languageCode " +
            "AND (:search = '' OR LOWER(v.sourceText) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "     OR LOWER(v.targetText) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<VocabItem> searchByLanguage(@Param("languageCode") String languageCode,
                                      @Param("search") String search,
                                      Pageable pageable);

    /**
     * Cekim/aspect paneli icin: kelimeyi, esleşen fiiliyle (aspectPair) birlikte
     * eager cekiyoruz. LEFT JOIN cunku aspectPair null olabilir.
     */
    @Query("SELECT v FROM VocabItem v LEFT JOIN FETCH v.aspectPair WHERE v.id = :id")
    Optional<VocabItem> findWithAspectPairById(@Param("id") Long id);
}
