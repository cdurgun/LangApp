-- ==========================================================
-- Ek kelime seti 2: "Dış Görünüş" konusuna sıfatlar (Rusça, A2)
-- Uygula: psql -U langapp_user -d langapp -f seed_appearance_2.sql
-- Not: "Dış Görünüş" konusu daha once yoksa (seed_appearance.sql
-- calistirilmadiysa) bu script de olusturur.
-- ==========================================================

INSERT INTO topics (language_id, name, level)
SELECT l.id, 'Dış Görünüş', 'A2'
FROM languages l
WHERE l.code = 'ru'
  AND NOT EXISTS (
      SELECT 1 FROM topics t
      WHERE t.language_id = l.id AND t.name = 'Dış Görünüş'
  );

INSERT INTO vocab_items (topic_id, source_text, target_text, example_sentence, word_type)
SELECT t.id, v.source_text, v.target_text, v.example_sentence, v.word_type
FROM topics t
JOIN languages l ON t.language_id = l.id AND l.code = 'ru' AND t.name = 'Dış Görünüş'
CROSS JOIN (VALUES
    ('краса́вец',         'yakışıklı adam',   'Он настоящий красавец.', 'NOUN'),
    ('брюне́т',           'esmer (erkek)',    'Мой брат брюнет.', 'NOUN'),
    ('блонди́н',          'sarışın (erkek)',  'Его друг блондин.', 'NOUN'),
    ('огро́мный',         'kocaman',          'У него огромный дом.', 'ADJECTIVE'),
    ('симпати́чный',      'sempatik',         'Она очень симпатичная девушка.', 'ADJECTIVE'),
    ('привлека́тельный',  'çekici',           'Он привлекательный мужчина.', 'ADJECTIVE'),
    ('худо́й',            'zayıf',            'Мальчик очень худой.', 'ADJECTIVE'),
    ('то́лстый',          'şişman',           'Кот у нас толстый.', 'ADJECTIVE'),
    ('стро́йный',         'ince yapılı',      'У неё стройная фигура.', 'ADJECTIVE'),
    ('по́лный',           'dolgun',           'Он немного полный.', 'ADJECTIVE'),
    ('кашта́новый',       'kestane rengi',    'У неё каштановые волосы.', 'ADJECTIVE'),
    ('ры́жий',            'kızıl (saç)',      'У него рыжие волосы.', 'ADJECTIVE'),
    ('прямо́й',           'düz (saç)',        'У меня прямые волосы.', 'ADJECTIVE'),
    ('кудря́вый',         'kıvırcık',         'У сестры кудрявые волосы.', 'ADJECTIVE'),
    ('ка́рий',            'ela (göz)',        'У неё карие глаза.', 'ADJECTIVE'),
    ('седо́й',            'kır saçlı',        'Мой дедушка совсем седой.', 'ADJECTIVE'),
    ('лы́сый',            'kel',              'Мой отец лысый.', 'ADJECTIVE'),
    ('тво́рческий',       'yaratıcı',         'Он очень творческий человек.', 'ADJECTIVE'),
    ('знако́м',           'tanıdık',          'Этот человек мне знаком.', 'ADJECTIVE'),
    ('похо́ж',            'benzer',           'Ты похож на своего отца.', 'ADJECTIVE')
) AS v(source_text, target_text, example_sentence, word_type);
