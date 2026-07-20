package com.langapp.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vocab_items")
@Getter
@Setter
@NoArgsConstructor
public class VocabItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    /** Hedef dildeki kelime/ifade, orn. "apple" */
    @Column(name = "source_text", nullable = false)
    private String sourceText;

    /** Ana dildeki karsiligi, orn. "elma" */
    @Column(name = "target_text", nullable = false)
    private String targetText;

    @Column(name = "example_sentence")
    private String exampleSentence;
}
