package com.langapp.content;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VocabBrowseService {

    private static final int PAGE_SIZE = 10;

    private final VocabItemRepository vocabItemRepository;
    private final VerbConjugationRepository verbConjugationRepository;
    private final MessageSource messageSource;

    public VocabBrowseService(VocabItemRepository vocabItemRepository,
                               VerbConjugationRepository verbConjugationRepository,
                               MessageSource messageSource) {
        this.vocabItemRepository = vocabItemRepository;
        this.verbConjugationRepository = verbConjugationRepository;
        this.messageSource = messageSource;
    }

    /**
     * Kelime listesi ekrani icin sayfalanmis sonuc dondurur.
     * @param page 0 tabanli sayfa numarasi
     * @param search bos ya da null olabilir; doluysa kelime/karsilik icinde arar
     */
    public Page<VocabItem> browse(String languageCode, int page, String search) {
        String normalizedSearch = search == null ? "" : search.trim();
        PageRequest pageRequest = PageRequest.of(Math.max(page, 0), PAGE_SIZE, Sort.by("sourceText").ascending());
        return vocabItemRepository.searchByLanguage(languageCode, normalizedSearch, pageRequest);
    }

    /**
     * Cekim paneli icin veri hazirlar: cekim kaydi (varsa) + aspect esleşme bilgisi (varsa).
     * Ikisi de yoksa (ne cekim ne aspect bilgisi girilmemisse) bos doner ve panelde
     * "eklenmemis" mesaji gosterilir. Aspect etiketi kullanicinin o anki diline
     * (LocaleContextHolder) gore cevrilmis olarak dondurulur.
     */
    public Optional<ConjugationView> getConjugation(Long vocabItemId) {
        VocabItem item = vocabItemRepository.findWithAspectPairById(vocabItemId).orElse(null);
        if (item == null) {
            return Optional.empty();
        }

        VerbConjugation conjugation = verbConjugationRepository.findByVocabItemId(vocabItemId).orElse(null);

        if (conjugation == null && item.getAspect() == null) {
            return Optional.empty();
        }

        String aspectLabel = null;
        if (item.getAspect() != null) {
            String key = "aspect." + item.getAspect().name();
            aspectLabel = messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
        }

        return Optional.of(ConjugationView.build(conjugation, item, aspectLabel));
    }
}
