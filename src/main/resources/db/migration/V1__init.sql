-- ==========================================================
-- V1: Baseline sema (tum entity'lerin karsiligi)
-- Bu migration, daha once ddl-auto=update ile olusturulmus semanin
-- ayni birebir karsiligidir. Flyway artik semayi bu dosyalar
-- uzerinden yonetecek.
-- ==========================================================

CREATE TABLE languages (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(5) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE topics (
    id BIGSERIAL PRIMARY KEY,
    language_id BIGINT NOT NULL REFERENCES languages(id),
    name VARCHAR(100) NOT NULL,
    level VARCHAR(5) NOT NULL
);
CREATE INDEX idx_topics_language ON topics(language_id);

CREATE TABLE vocab_items (
    id BIGSERIAL PRIMARY KEY,
    topic_id BIGINT NOT NULL REFERENCES topics(id),
    source_text TEXT NOT NULL,
    target_text TEXT NOT NULL,
    example_sentence TEXT,
    word_type VARCHAR(20),
    aspect VARCHAR(20),
    aspect_pair_id BIGINT REFERENCES vocab_items(id)
);
CREATE INDEX idx_vocab_items_topic ON vocab_items(topic_id);

CREATE TABLE quiz_questions (
    id BIGSERIAL PRIMARY KEY,
    topic_id BIGINT NOT NULL REFERENCES topics(id),
    question_text TEXT NOT NULL,
    correct_answer TEXT NOT NULL,
    option_2 TEXT NOT NULL,
    option_3 TEXT NOT NULL,
    option_4 TEXT NOT NULL
);
CREATE INDEX idx_quiz_questions_topic ON quiz_questions(topic_id);

CREATE TABLE translation_exercises (
    id BIGSERIAL PRIMARY KEY,
    topic_id BIGINT NOT NULL REFERENCES topics(id),
    source_text TEXT NOT NULL,
    source_lang VARCHAR(5) NOT NULL,
    target_lang VARCHAR(5) NOT NULL,
    expected_translation TEXT NOT NULL,
    hint TEXT
);
CREATE INDEX idx_translation_exercises_topic ON translation_exercises(topic_id);

CREATE TABLE verb_conjugations (
    id BIGSERIAL PRIMARY KEY,
    vocab_item_id BIGINT NOT NULL UNIQUE REFERENCES vocab_items(id),
    present_1s TEXT,
    present_2s TEXT,
    present_3s TEXT,
    present_1p TEXT,
    present_2p TEXT,
    present_3p TEXT,
    future_1s TEXT,
    future_2s TEXT,
    future_3s TEXT,
    future_1p TEXT,
    future_2p TEXT,
    future_3p TEXT,
    imperative_singular TEXT,
    imperative_plural TEXT
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    target_language VARCHAR(5),
    created_at TIMESTAMP NOT NULL,
    current_streak INT NOT NULL DEFAULT 0,
    longest_streak INT NOT NULL DEFAULT 0,
    last_active_date DATE
);

CREATE TABLE user_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    topic_id BIGINT NOT NULL REFERENCES topics(id),
    mastery_level INT NOT NULL DEFAULT 0,
    last_practiced_at TIMESTAMP,
    UNIQUE (user_id, topic_id)
);
CREATE INDEX idx_user_progress_user ON user_progress(user_id);

CREATE TABLE attempts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    exercise_type VARCHAR(20) NOT NULL,
    exercise_id BIGINT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    user_answer TEXT,
    answered_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_attempts_user ON attempts(user_id);
