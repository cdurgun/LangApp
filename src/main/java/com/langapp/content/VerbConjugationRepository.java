package com.langapp.content;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerbConjugationRepository extends JpaRepository<VerbConjugation, Long> {
    Optional<VerbConjugation> findByVocabItemId(Long vocabItemId);
}
