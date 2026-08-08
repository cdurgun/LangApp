package com.langapp.admin;

import com.langapp.content.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminWordService {

    private final VocabItemRepository vocabItemRepository;
    private final TopicRepository topicRepository;
    private final VerbConjugationRepository verbConjugationRepository;

    public AdminWordService(VocabItemRepository vocabItemRepository,
                             TopicRepository topicRepository,
                             VerbConjugationRepository verbConjugationRepository) {
        this.vocabItemRepository = vocabItemRepository;
        this.topicRepository = topicRepository;
        this.verbConjugationRepository = verbConjugationRepository;
    }

    @Transactional
    public void addWord(WordForm form) {
        Topic topic = topicRepository.findById(form.getTopicId())
                .orElseThrow(() -> new IllegalArgumentException("Konu bulunamadi: " + form.getTopicId()));

        VocabItem item = new VocabItem();
        item.setTopic(topic);
        item.setSourceText(form.getSourceText().trim());
        item.setTargetText(form.getTargetText().trim());
        item.setExampleSentence(blankToNull(form.getExampleSentence()));
        item.setAudioUrl(blankToNull(form.getAudioUrl()));

        if (form.getWordType() != null && !form.getWordType().isBlank()) {
            item.setWordType(WordType.valueOf(form.getWordType()));
        }
        if (form.getAspect() != null && !form.getAspect().isBlank()) {
            item.setAspect(VerbAspect.valueOf(form.getAspect()));
        }

        vocabItemRepository.save(item);

        // Aspect esi secildiyse, karsilikli baglantiyi kur (iki taraf da birbirine isaret etsin).
        if (form.getPairId() != null) {
            VocabItem pair = vocabItemRepository.findById(form.getPairId())
                    .orElseThrow(() -> new IllegalArgumentException("Esleşen fiil bulunamadi: " + form.getPairId()));
            item.setAspectPair(pair);
            pair.setAspectPair(item);
            vocabItemRepository.save(item);
            vocabItemRepository.save(pair);
        }
    }

    /**
     * Bir kelimeyi guvenli sekilde siler: once ona ait cekim kaydini,
     * sonra baska bir kelimenin ona isaret eden aspect_pair_id referansini
     * temizler (FK ihlali olmasin diye), en son kelimeyi siler.
     */
    @Transactional
    public void deleteWord(Long id) {
        VocabItem item = vocabItemRepository.findWithAspectPairById(id).orElse(null);
        if (item == null) {
            return;
        }

        verbConjugationRepository.findByVocabItemId(id)
                .ifPresent(verbConjugationRepository::delete);

        if (item.getAspectPair() != null) {
            VocabItem pair = item.getAspectPair();
            pair.setAspectPair(null);
            vocabItemRepository.save(pair);
        }

        // Guvenlik agi: reciprocal olmayan (tek yonlu) bir referans varsa onu da temizle.
        for (VocabItem referring : vocabItemRepository.findByAspectPairId(id)) {
            referring.setAspectPair(null);
            vocabItemRepository.save(referring);
        }

        vocabItemRepository.deleteById(id);
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
