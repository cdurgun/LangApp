-- ==========================================================
-- Ornek fiil + cekim ekleme scripti (Rusca, vurgulu)
-- Uygula: psql -U langapp_user -d langapp -f seed_verbs.sql
--
-- ONEMLI DILBILGISI NOTU:
-- Rusca fiiller "tamamlanmamis" (imperfective) ve "tamamlanmis"
-- (perfective) olmak uzere ikiye ayrilir:
--   - Tamamlanmamis fiiller (govori't', chita't') simdiki zamana
--     sahiptir; gelecek zaman "buду + mastar" seklinde BILESIK kurulur.
--   - Tamamlanmis fiiller (skaza't') simdiki zamana sahip DEGILDIR
--     (present_* alanlari bu yuzden NULL); "gelecek zaman" gorunumlu
--     cekimleri aslinda BASIT (tek kelime) formdur ve konusma dilinde
--     hem "bir kerelik gelecekteki eylem" anlamini tasir.
-- Bu ayrimi ogrenciye gostermek icin ucu de tabloya farkli sekillerde
-- eklendi.
-- ==========================================================

-- --- Konu: yoksa olustur ---
INSERT INTO topics (language_id, name, level)
SELECT l.id, 'Fiiller', 'A2'
FROM languages l
WHERE l.code = 'ru'
  AND NOT EXISTS (
      SELECT 1 FROM topics t WHERE t.language_id = l.id AND t.name = 'Fiiller'
  );

-- --- Fiil kelimeleri (word_type = VERB) ---
INSERT INTO vocab_items (topic_id, source_text, target_text, example_sentence, word_type)
SELECT t.id, v.source_text, v.target_text, v.example_sentence, 'VERB'
FROM topics t
JOIN languages l ON t.language_id = l.id AND l.code = 'ru' AND t.name = 'Fiiller'
CROSS JOIN (VALUES
    ('говори́ть', 'konuşmak (sürekli/tamamlanmamış)', 'Я говорю́ по-ру́сски.'),
    ('чита́ть',   'okumak (sürekli/tamamlanmamış)',   'Я чита́ю интере́сную кни́гу.'),
    ('сказа́ть',  'söylemek (bir kerelik/tamamlanmış)', 'Скажи́ мне пра́вду.')
) AS v(source_text, target_text, example_sentence)
WHERE NOT EXISTS (
    SELECT 1 FROM vocab_items existing WHERE existing.source_text = v.source_text
);

-- --- Çekim: говори́ть (tamamlanmamış — şimdiki zamanı var, gelecek zaman bileşik) ---
INSERT INTO verb_conjugations (
    vocab_item_id,
    present_1s, present_2s, present_3s, present_1p, present_2p, present_3p,
    future_1s, future_2s, future_3s, future_1p, future_2p, future_3p,
    imperative_singular, imperative_plural
)
SELECT vi.id,
    'говорю́', 'говори́шь', 'говори́т', 'говори́м', 'говори́те', 'говоря́т',
    'бу́ду говори́ть', 'бу́дешь говори́ть', 'бу́дет говори́ть', 'бу́дем говори́ть', 'бу́дете говори́ть', 'бу́дут говори́ть',
    'говори́', 'говори́те'
FROM vocab_items vi
WHERE vi.source_text = 'говори́ть'
  AND NOT EXISTS (SELECT 1 FROM verb_conjugations vc WHERE vc.vocab_item_id = vi.id);

-- --- Çekim: чита́ть (tamamlanmamış) ---
INSERT INTO verb_conjugations (
    vocab_item_id,
    present_1s, present_2s, present_3s, present_1p, present_2p, present_3p,
    future_1s, future_2s, future_3s, future_1p, future_2p, future_3p,
    imperative_singular, imperative_plural
)
SELECT vi.id,
    'чита́ю', 'чита́ешь', 'чита́ет', 'чита́ем', 'чита́ете', 'чита́ют',
    'бу́ду чита́ть', 'бу́дешь чита́ть', 'бу́дет чита́ть', 'бу́дем чита́ть', 'бу́дете чита́ть', 'бу́дут чита́ть',
    'чита́й', 'чита́йте'
FROM vocab_items vi
WHERE vi.source_text = 'чита́ть'
  AND NOT EXISTS (SELECT 1 FROM verb_conjugations vc WHERE vc.vocab_item_id = vi.id);

-- --- Çekim: сказа́ть (tamamlanmış — şimdiki zaman YOK, "gelecek" basit çekim) ---
INSERT INTO verb_conjugations (
    vocab_item_id,
    present_1s, present_2s, present_3s, present_1p, present_2p, present_3p,
    future_1s, future_2s, future_3s, future_1p, future_2p, future_3p,
    imperative_singular, imperative_plural
)
SELECT vi.id,
    NULL, NULL, NULL, NULL, NULL, NULL,
    'скажу́', 'ска́жешь', 'ска́жет', 'ска́жем', 'ска́жете', 'ска́жут',
    'скажи́', 'скажи́те'
FROM vocab_items vi
WHERE vi.source_text = 'сказа́ть'
  AND NOT EXISTS (SELECT 1 FROM verb_conjugations vc WHERE vc.vocab_item_id = vi.id);
