# LangApp — İngilizce / Rusça Pratik Uygulaması

Spring Boot 3 + Spring MVC + Thymeleaf + Spring Security + PostgreSQL ile
yazılmış, kelime kartı / quiz / çeviri pratiği yapılabilen bir dil öğrenme
uygulaması iskeleti.

## Gereksinimler
- Java 21
- Maven 3.9+
- PostgreSQL 14+

## Kurulum

### 1. Veritabanını oluştur
```sql
CREATE DATABASE langapp;
CREATE USER langapp_user WITH PASSWORD 'changeme';
GRANT ALL PRIVILEGES ON DATABASE langapp TO langapp_user;
```

`src/main/resources/application.properties` içindeki bağlantı bilgilerini
kendi ortamına göre güncelle (`spring.datasource.*`).

### 2. Uygulamayı ilk kez çalıştır (tabloları otomatik oluşturması için)
```bash
mvn spring-boot:run
```
`spring.jpa.hibernate.ddl-auto=update` ayarı sayesinde entity'lerden
tablolar otomatik oluşur. Uygulamayı bir kez ayağa kaldırıp durdurman yeterli.

> Not: Bu ayar sadece geliştirme içindir. Prod'a geçerken `validate`'e çevirip
> şema değişikliklerini Flyway/Liquibase gibi bir migration aracıyla
> yönetmeni öneririm.

### 3. Örnek içeriği yükle
```bash
psql -U langapp_user -d langapp -f src/main/resources/db/seed.sql
```
Bu script İngilizce/Rusça için birkaç kelime, quiz sorusu ve çeviri
alıştırması ekler. Kendi içeriğini de aynı formatta SQL ile veya CSV'den
`\copy` komutuyla yükleyebilirsin.

### 4. Uygulamayı tekrar çalıştır
```bash
mvn spring-boot:run
```
Tarayıcıda `http://localhost:8080` adresine git, `/register` üzerinden
kayıt ol (İngilizce ya da Rusça seç), giriş yap ve pratik yapmaya başla.

## Proje Yapısı
```
com.langapp
├── config       → SecurityConfig (form login, session bazlı auth)
├── user         → User entity, kayıt/login, streak takibi
├── content      → Language, Topic, VocabItem, QuizQuestion, TranslationExercise
├── progress     → UserProgress (mastery %), Attempt (deneme logu)
├── practice     → PracticeService, AnswerCheckService (fuzzy match), PracticeController
└── web          → AuthController, DashboardController
```

## Sonraki Adımlar İçin Fikirler
- Spaced repetition (SM-2 algoritması) — şu an basit mastery % kullanılıyor
- Sesli telaffuz (audio_url alanı VocabItem'da hazır, dosya/servis eklenebilir)
- Quiz'i "N soruluk oturum" şeklinde ilerletmek (şu an her seferinde rastgele 1 soru gösteriyor)
- Admin paneli ile içerik yönetimi (şu an SQL/CSV ile manuel yükleme var)
