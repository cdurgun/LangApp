package com.langapp.content;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    @Column(nullable = false, length = 100)
    private String name;

    /** CEFR seviyesi: A1, A2, B1, B2, C1, C2 */
    @Column(nullable = false, length = 5)
    private String level;

    public Topic(Language language, String name, String level) {
        this.language = language;
        this.name = name;
        this.level = level;
    }
}
