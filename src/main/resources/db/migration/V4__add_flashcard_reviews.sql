-- V4: Flashcard'lar icin kelime-bazinda spaced repetition (SM-2) durumu.
-- Her (kullanici, kelime) cifti icin ayri bir tekrar zamanlamasi tutulur.
-- Satir yoksa o kelime o kullanici icin hic pratik edilmemis demektir -
-- bu durumda "bugun tekrar edilmeli" olarak kabul edilir (yeni kelime).
CREATE TABLE flashcard_reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    vocab_item_id BIGINT NOT NULL REFERENCES vocab_items(id),
    ease_factor DOUBLE PRECISION NOT NULL DEFAULT 2.5,
    interval_days INT NOT NULL DEFAULT 0,
    repetitions INT NOT NULL DEFAULT 0,
    next_review_date DATE NOT NULL,
    last_reviewed_at TIMESTAMP,
    UNIQUE (user_id, vocab_item_id)
);
CREATE INDEX idx_flashcard_reviews_user ON flashcard_reviews(user_id);
CREATE INDEX idx_flashcard_reviews_due ON flashcard_reviews(user_id, next_review_date);
