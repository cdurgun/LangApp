package com.langapp.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WordForm {

    @NotNull(message = "{admin.words.error.topicRequired}")
    private Long topicId;

    @NotBlank(message = "{admin.words.error.sourceRequired}")
    private String sourceText;

    @NotBlank(message = "{admin.words.error.targetRequired}")
    private String targetText;

    private String exampleSentence;

    /** WordType enum adi (orn. "NOUN"), bos birakilabilir */
    private String wordType;

    /** VerbAspect enum adi (orn. "IMPERFECTIVE"), sadece fiil icin anlamli, bos birakilabilir */
    private String aspect;

    /** Aspect esi olan mevcut fiilin id'si, opsiyonel */
    private Long pairId;

    /** Opsiyonel, elle girilmis ses dosyasi linki - doluysa TTS yerine bu calinir */
    private String audioUrl;
}
