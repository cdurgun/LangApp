-- V2: Admin yetkisi icin alan ekleniyor.
-- Tek admin senaryosu icin ayri bir rol tablosu yerine basit bir boolean yeterli.
ALTER TABLE users ADD COLUMN is_admin BOOLEAN NOT NULL DEFAULT FALSE;
