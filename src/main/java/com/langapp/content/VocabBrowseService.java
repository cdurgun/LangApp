package com.langapp.content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class VocabBrowseService {

    private static final int PAGE_SIZE = 10;

    private final VocabItemRepository vocabItemRepository;

    public VocabBrowseService(VocabItemRepository vocabItemRepository) {
        this.vocabItemRepository = vocabItemRepository;
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
}
