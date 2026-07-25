-- ==========================================================
-- Geriye donuk doldurma: daha once seed.sql / seed_appearance.sql /
-- seed_appearance_2.sql calistirdiysan, o kayitlarda word_type
-- alani bos (NULL) kalmis olacak. Bu script kelime metnine gore
-- eslesenleri gunceller.
--
-- Not: word_type kolonu yoksa (uygulamayi hic yeniden baslatmadiysan)
-- once uygulamayi bir kez calistir (ddl-auto=update kolonu otomatik ekler),
-- sonra bu scripti uygula:
--   psql -U langapp_user -d langapp -f update_word_types.sql
-- ==========================================================

UPDATE vocab_items SET word_type = 'NOUN' WHERE source_text IN (
    'apple', 'house', 'water', 'friend', 'book',
    'яблоко', 'дом', 'вода', 'друг', 'книга',
    'во́лосы', 'усы́', 'борода́', 'очки́', 'улы́бка', 'рост',
    'парикма́херская', 'парикма́хер', 'причёска', 'стри́жка', 'расчёска',
    'краса́вец', 'брюне́т', 'блонди́н'
) AND word_type IS NULL;

UPDATE vocab_items SET word_type = 'ADJECTIVE' WHERE source_text IN (
    'огро́мный', 'симпати́чный', 'привлека́тельный', 'худо́й', 'то́лстый',
    'стро́йный', 'по́лный', 'кашта́новый', 'ры́жий', 'прямо́й', 'кудря́вый',
    'ка́рий', 'седо́й', 'лы́сый', 'тво́рческий', 'знако́м', 'похо́ж'
) AND word_type IS NULL;
