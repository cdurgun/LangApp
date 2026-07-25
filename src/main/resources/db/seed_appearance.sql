-- ==========================================================
-- Ek kelime seti: "Dış Görünüş" konusu (Rusça, A2)
-- Uygula: psql -U langapp_user -d langapp -f seed_appearance.sql
-- ==========================================================

-- Once konuyu olustur (yoksa)
INSERT INTO topics (language_id, name, level)
SELECT l.id, 'Dış Görünüş', 'A2'
FROM languages l
WHERE l.code = 'ru'
  AND NOT EXISTS (
      SELECT 1 FROM topics t
      WHERE t.language_id = l.id AND t.name = 'Dış Görünüş'
  );

-- Kelimeleri ekle
INSERT INTO vocab_items (topic_id, source_text, target_text, example_sentence, word_type)
SELECT t.id, v.source_text, v.target_text, v.example_sentence, v.word_type
FROM topics t
JOIN languages l ON t.language_id = l.id AND l.code = 'ru' AND t.name = 'Dış Görünüş'
CROSS JOIN (VALUES
    ('во́лосы',        'saç',              'У неё длинные волосы.', 'NOUN'),
    ('усы́',           'bıyık',            'У него чёрные усы.', 'NOUN'),
    ('борода́',        'sakal',            'Его борода очень густая.', 'NOUN'),
    ('очки́',          'gözlük',           'Я ношу очки для чтения.', 'NOUN'),
    ('улы́бка',        'gülümseme',        'У неё красивая улыбка.', 'NOUN'),
    ('рост',           'boy (uzunluk)',    'Какой у тебя рост?', 'NOUN'),
    ('парикма́херская', 'kuaför (salon)',   'Я иду в парикмахерскую.', 'NOUN'),
    ('парикма́хер',     'kuaför (kişi)',    'Мой парикмахер работает быстро.', 'NOUN'),
    ('причёска',       'saç modeli',       'Мне нравится твоя новая причёска.', 'NOUN'),
    ('стри́жка',        'saç kesimi',       'Мне нужна стрижка.', 'NOUN'),
    ('расчёска',       'tarak',            'Дай мне, пожалуйста, расчёску.', 'NOUN')
) AS v(source_text, target_text, example_sentence, word_type);
