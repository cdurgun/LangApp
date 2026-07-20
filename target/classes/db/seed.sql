-- ==========================================================
-- LangApp ornek icerik yukleme scripti
-- Uygulamayi bir kez calistirip (ddl-auto=update ile tablolar
-- otomatik olusur) sonra bu scripti PostgreSQL'e uygula:
--   psql -U langapp_user -d langapp -f seed.sql
-- ==========================================================

INSERT INTO languages (code, name) VALUES
    ('en', 'İngilizce'),
    ('ru', 'Rusça')
ON CONFLICT (code) DO NOTHING;

-- --- Konular ---
INSERT INTO topics (language_id, name, level)
SELECT id, 'Temel Kelimeler', 'A1' FROM languages WHERE code = 'en';
INSERT INTO topics (language_id, name, level)
SELECT id, 'Günlük Konuşma', 'A2' FROM languages WHERE code = 'en';
INSERT INTO topics (language_id, name, level)
SELECT id, 'Temel Kelimeler', 'A1' FROM languages WHERE code = 'ru';
INSERT INTO topics (language_id, name, level)
SELECT id, 'Günlük Konuşma', 'A2' FROM languages WHERE code = 'ru';

-- --- İngilizce kelime kartları (Temel Kelimeler / A1) ---
INSERT INTO vocab_items (topic_id, source_text, target_text, example_sentence)
SELECT t.id, v.source_text, v.target_text, v.example_sentence
FROM topics t
JOIN languages l ON t.language_id = l.id AND l.code = 'en' AND t.level = 'A1'
CROSS JOIN (VALUES
    ('apple', 'elma', 'I eat an apple every morning.'),
    ('house', 'ev', 'This is my house.'),
    ('water', 'su', 'Can I have some water?'),
    ('friend', 'arkadaş', 'She is my best friend.'),
    ('book', 'kitap', 'I am reading a book.')
) AS v(source_text, target_text, example_sentence);

-- --- Rusça kelime kartları (Temel Kelimeler / A1) ---
INSERT INTO vocab_items (topic_id, source_text, target_text, example_sentence)
SELECT t.id, v.source_text, v.target_text, v.example_sentence
FROM topics t
JOIN languages l ON t.language_id = l.id AND l.code = 'ru' AND t.level = 'A1'
CROSS JOIN (VALUES
    ('яблоко', 'elma', 'Я ем яблоко каждое утро.'),
    ('дом', 'ev', 'Это мой дом.'),
    ('вода', 'su', 'Можно мне немного воды?'),
    ('друг', 'arkadaş', 'Она моя лучшая подруга.'),
    ('книга', 'kitap', 'Я читаю книгу.')
) AS v(source_text, target_text, example_sentence);

-- --- İngilizce quiz soruları ---
INSERT INTO quiz_questions (topic_id, question_text, correct_answer, option_2, option_3, option_4)
SELECT t.id, q.question_text, q.correct_answer, q.option_2, q.option_3, q.option_4
FROM topics t
JOIN languages l ON t.language_id = l.id AND l.code = 'en' AND t.level = 'A1'
CROSS JOIN (VALUES
    ('"Elma" kelimesinin İngilizcesi nedir?', 'apple', 'orange', 'banana', 'grape'),
    ('"Ev" kelimesinin İngilizcesi nedir?', 'house', 'car', 'tree', 'road'),
    ('"Su" kelimesinin İngilizcesi nedir?', 'water', 'milk', 'juice', 'tea')
) AS q(question_text, correct_answer, option_2, option_3, option_4);

-- --- Rusça quiz soruları ---
INSERT INTO quiz_questions (topic_id, question_text, correct_answer, option_2, option_3, option_4)
SELECT t.id, q.question_text, q.correct_answer, q.option_2, q.option_3, q.option_4
FROM topics t
JOIN languages l ON t.language_id = l.id AND l.code = 'ru' AND t.level = 'A1'
CROSS JOIN (VALUES
    ('"Elma" kelimesinin Rusçası nedir?', 'яблоко', 'апельсин', 'банан', 'виноград'),
    ('"Ev" kelimesinin Rusçası nedir?', 'дом', 'машина', 'дерево', 'дорога'),
    ('"Su" kelimesinin Rusçası nedir?', 'вода', 'молоко', 'сок', 'чай')
) AS q(question_text, correct_answer, option_2, option_3, option_4);

-- --- İngilizce çeviri alıştırmaları ---
INSERT INTO translation_exercises (topic_id, source_text, source_lang, target_lang, expected_translation, hint)
SELECT t.id, e.source_text, 'tr', 'en', e.expected_translation, e.hint
FROM topics t
JOIN languages l ON t.language_id = l.id AND l.code = 'en' AND t.level = 'A2'
CROSS JOIN (VALUES
    ('Bugün hava çok güzel.', 'The weather is very nice today.', 'weather = hava'),
    ('Adım Ahmet.', 'My name is Ahmet.', NULL),
    ('Yarın seninle buluşacağım.', 'I will meet you tomorrow.', 'buluşmak = to meet')
) AS e(source_text, expected_translation, hint);

-- --- Rusça çeviri alıştırmaları ---
INSERT INTO translation_exercises (topic_id, source_text, source_lang, target_lang, expected_translation, hint)
SELECT t.id, e.source_text, 'tr', 'ru', e.expected_translation, e.hint
FROM topics t
JOIN languages l ON t.language_id = l.id AND l.code = 'ru' AND t.level = 'A2'
CROSS JOIN (VALUES
    ('Bugün hava çok güzel.', 'Сегодня очень хорошая погода.', 'погода = hava'),
    ('Adım Ahmet.', 'Меня зовут Ахмет.', NULL),
    ('Yarın seninle buluşacağım.', 'Я встречусь с тобой завтра.', 'встретиться = buluşmak')
) AS e(source_text, expected_translation, hint);
