-- ==========================================================
-- Fiil gorunusu (aspect) atama scripti
-- On kosul: uygulamayi bir kez calistirmis olman lazim (aspect ve
-- aspect_pair_id kolonlari ddl-auto=update ile otomatik eklenir).
-- Uygula: psql -U langapp_user -d langapp -f seed_verb_aspects.sql
--
-- Bu script su ciftleri kurar:
--   говори́ть (bitmemis) <-> сказа́ть (bitmis)   [ikisi de zaten vardi]
--   чита́ть   (bitmemis) <-> прочита́ть (bitmis)  [прочитать burada eklendi]
-- ==========================================================

-- --- Eksik olan прочита́ть fiilini "Fiiller" konusuna ekle ---
INSERT INTO vocab_items (topic_id, source_text, target_text, example_sentence, word_type, aspect)
SELECT t.id, 'прочита́ть', 'okumak (bitirmek, bir kerelik)', 'Я прочита́ю э́ту кни́гу за́втра.', 'VERB', 'PERFECTIVE'
FROM topics t
JOIN languages l ON t.language_id = l.id AND l.code = 'ru' AND t.name = 'Fiiller'
WHERE NOT EXISTS (
    SELECT 1 FROM vocab_items existing WHERE existing.source_text = 'прочита́ть'
);

-- --- прочита́ть icin cekim (bitmis fiil -> simdiki zaman yok, gelecek basit cekim) ---
INSERT INTO verb_conjugations (
    vocab_item_id,
    present_1s, present_2s, present_3s, present_1p, present_2p, present_3p,
    future_1s, future_2s, future_3s, future_1p, future_2p, future_3p,
    imperative_singular, imperative_plural
)
SELECT vi.id,
    NULL, NULL, NULL, NULL, NULL, NULL,
    'прочита́ю', 'прочита́ешь', 'прочита́ет', 'прочита́ем', 'прочита́ете', 'прочита́ют',
    'прочита́й', 'прочита́йте'
FROM vocab_items vi
WHERE vi.source_text = 'прочита́ть'
  AND NOT EXISTS (SELECT 1 FROM verb_conjugations vc WHERE vc.vocab_item_id = vi.id);

-- --- Aspect atamalari (daha once NULL kalmis olan kayitlar icin) ---
UPDATE vocab_items SET aspect = 'IMPERFECTIVE' WHERE source_text = 'говори́ть' AND aspect IS NULL;
UPDATE vocab_items SET aspect = 'PERFECTIVE'   WHERE source_text = 'сказа́ть'  AND aspect IS NULL;
UPDATE vocab_items SET aspect = 'IMPERFECTIVE' WHERE source_text = 'чита́ть'   AND aspect IS NULL;
-- прочита́ть zaten INSERT sirasinda PERFECTIVE olarak eklendi.

-- --- Karsilikli esleme (her iki tarafa da diger fiilin id'sini yaz) ---
UPDATE vocab_items SET aspect_pair_id = (SELECT id FROM vocab_items WHERE source_text = 'сказа́ть')
WHERE source_text = 'говори́ть' AND aspect_pair_id IS NULL;

UPDATE vocab_items SET aspect_pair_id = (SELECT id FROM vocab_items WHERE source_text = 'говори́ть')
WHERE source_text = 'сказа́ть' AND aspect_pair_id IS NULL;

UPDATE vocab_items SET aspect_pair_id = (SELECT id FROM vocab_items WHERE source_text = 'прочита́ть')
WHERE source_text = 'чита́ть' AND aspect_pair_id IS NULL;

UPDATE vocab_items SET aspect_pair_id = (SELECT id FROM vocab_items WHERE source_text = 'чита́ть')
WHERE source_text = 'прочита́ть' AND aspect_pair_id IS NULL;
