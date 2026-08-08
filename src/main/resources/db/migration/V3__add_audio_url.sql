-- V3: Kelimelere opsiyonel bir ses dosyasi linki eklenebilsin diye.
-- Doluysa, on yuzde tarayici TTS yerine bu dosya calinir (daha yuksek kalite,
-- ozellikle vurgusu kritik kelimeler icin).
ALTER TABLE vocab_items ADD COLUMN audio_url TEXT;
