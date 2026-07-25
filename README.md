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

### 2. Uygulamayı çalıştır (Flyway tabloları otomatik oluşturur)
```bash
mvn spring-boot:run
```
Artık şema `src/main/resources/db/migration/V1__init.sql` üzerinden Flyway
tarafından yönetiliyor — uygulama ilk açılışta migration'ı otomatik uygular.
İleride şema değişikliği gerektiğinde (yeni kolon/tablo) mevcut migration'ı
değiştirme, `V2__aciklama.sql` gibi yeni bir dosya ekle.

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

> **Önemli — mevcut yerel veritabanın varsa:** Daha önce `ddl-auto=update` ile
> oluşturulmuş bir yerel veritabanın varsa, Flyway "tablolar zaten var" diye
> hata verecektir. En kolay çözüm: yerel `langapp` veritabanını silip
> (`DROP DATABASE langapp;` ardından adım 1'i tekrar çalıştır) sıfırdan
> başlamak. Mevcut verini korumak istiyorsan `flyway baseline` komutuna bak.

## Canlıya Alma (Railway)

Bu proje Docker ile paketlenmiş durumda (`Dockerfile` kök dizinde), Railway
bunu otomatik algılayıp build eder.

### 1. Railway'de proje oluştur
- [railway.app](https://railway.app) üzerinde GitHub reponu bağlayarak yeni
  proje oluştur (repo'yu önce GitHub'a push etmen gerekiyor).
- Aynı projeye **"PostgreSQL"** servisini de ekle (Railway şablonlardan tek
  tıkla ekliyor).

### 2. Ortam değişkenlerini ayarla
Uygulama servisinin **Variables** sekmesine şunları ekle (Railway'in servisler
arası referans söz dizimini kullanarak, Postgres servisinin adının
`Postgres` olduğunu varsayıyoruz — kendi servis adınla değiştir):

```
SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
```

`PORT` değişkenini Railway zaten otomatik atıyor, elle eklemene gerek yok —
uygulama `server.port=${PORT:8080}` ile bunu otomatik okuyor.

### 3. Deploy et
Railway, `Dockerfile`'ı görüp otomatik build/deploy eder. İlk açılışta Flyway
migration'ı (`V1__init.sql`) boş veritabanına uygular, tablolar oluşur.

### 4. İçerik yükle
Deploy sonrası `seed.sql` ve diğer içerik script'lerini Railway'in Postgres
servisine karşı çalıştırman gerekiyor. Railway CLI ile:
```bash
railway link          # projeyi sec
railway connect postgres   # psql oturumu acar
```
Açılan `psql` oturumunda `\i src/main/resources/db/seed.sql` gibi dosya
içeriğini yapıştırıp çalıştırabilirsin (ya da içeriği kopyala-yapıştır yap).

### 5. Health check (opsiyonel ama önerilir)
Railway servis ayarlarında **Healthcheck Path**'i `/actuator/health` olarak
ayarla — bu sayede Railway, uygulama gerçekten ayağa kalkmadan trafiği
yönlendirmez.

### Notlar
- **Domain:** Railway sana `*.up.railway.app` uzantılı ücretsiz bir domain
  verir; kendi domainini bağlamak istersen Settings → Networking'den
  yapılandırabilirsin. HTTPS otomatik.
- **Loglar:** Railway dashboard'unda servisin "Deployments" sekmesinden
  canlı logları izleyebilirsin — hata ayıklamak için ilk bakılacak yer.
- **Maliyet:** Hobby plan $5/ay taban ücret; bu küçük bir uygulama + Postgres
  için genelde yeterli, aşımı olursa fatura o oranda artar.

## Sonraki Adımlar İçin Fikirler
- Spaced repetition (SM-2 algoritması) — şu an basit mastery % kullanılıyor
- Sesli telaffuz (audio_url alanı VocabItem'da hazır, dosya/servis eklenebilir)
- Quiz'i "N soruluk oturum" şeklinde ilerletmek (şu an her seferinde rastgele 1 soru gösteriyor)
- Admin paneli ile içerik yönetimi (şu an SQL/CSV ile manuel yükleme var)
