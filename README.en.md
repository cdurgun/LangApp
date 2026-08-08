# LangApp — English / Russian Practice App

A language-learning app built with Spring Boot 3 + Spring MVC + Thymeleaf +
Spring Security + PostgreSQL, featuring flashcards, quizzes, translation
practice, a bilingual (TR/EN) interface, and spoken pronunciation.

## Requirements
- Java 21
- Maven 3.9+
- PostgreSQL 14+

## Setup

### 1. Create the database
```sql
CREATE DATABASE langapp;
CREATE USER langapp_user WITH PASSWORD 'changeme';
GRANT ALL PRIVILEGES ON DATABASE langapp TO langapp_user;
```

Update the connection settings in `src/main/resources/application.properties`
(`spring.datasource.*`) to match your environment.

### 2. Run the application (Flyway creates the tables automatically)
```bash
mvn spring-boot:run
```
The schema is managed by Flyway via the files in
`src/main/resources/db/migration/`: `V1__init.sql`,
`V2__add_admin_flag.sql`, `V3__add_audio_url.sql` — the app applies all of
them in order automatically on first startup. When you need a future schema
change (new column/table), don't edit an existing migration — add a new
file like `V4__description.sql` instead.

### 3. Load sample content
```bash
psql -U langapp_user -d langapp -f src/main/resources/db/seed.sql
```
This script adds a handful of English/Russian words, quiz questions, and
translation exercises. You can load your own content the same way, either
via SQL, from a CSV using `\copy`, or through the in-app `/admin/words`
screen (see below).

### 4. Run the application again
```bash
mvn spring-boot:run
```
Go to `http://localhost:8080` in your browser, register via `/register`
(pick English or Russian), log in, and start practicing.

## Project Structure
```
com.langapp
├── config       → SecurityConfig (form login, session-based auth), LocaleConfig (language switching)
├── user         → User entity, registration/login, streak tracking, admin flag
├── content      → Language, Topic, VocabItem, QuizQuestion, TranslationExercise
├── progress     → UserProgress (mastery %), Attempt (attempt log)
├── practice     → PracticeService, AnswerCheckService (fuzzy match), PracticeController
├── admin        → Admin word management (add/delete words via /admin/words)
└── web          → AuthController, DashboardController
```

> **Important — if you already have a local database:** If you previously
> created a local database with `ddl-auto=update`, Flyway will fail with
> "tables already exist". The easiest fix is to drop the local `langapp`
> database and start fresh (`DROP DATABASE langapp;` then re-run step 1).
> If you want to keep your existing data instead, look into the
> `flyway baseline` command.

## Language Support (Bilingual UI)

The app's interface works in both Turkish and English. Click the
**"TR · EN"** links in the top-right corner to switch — the choice is
stored in a cookie (`langapp.locale`, 365 days), so it's remembered on your
next visit too.

Under the hood: `LocaleConfig` (a cookie-based `LocaleResolver` +
`LocaleChangeInterceptor`) intercepts every request and looks for a
`?lang=tr` / `?lang=en` parameter; screen text comes from
`src/main/resources/messages.properties` (Turkish, default) and
`messages_en.properties` (English). When adding new text, remember to add
it to both files — otherwise you'll see something like "?? key ??" in the
missing language.

## Spoken Pronunciation

Every word in the word list and flashcards has a 🔊 button next to it. It
works in two layers:

1. **Default: browser TTS (Web Speech API).** Free, no setup required. The
   `selectBestVoice()` function automatically picks the best-quality voice
   available for the target language (favoring ones labeled
   natural/neural/premium). Quality can vary depending on the user's OS/
   browser.
2. **Preferred: `audio_url` override.** If a word's `vocab_items` row has
   its `audio_url` field set (via the admin screen or SQL), the button
   plays that audio file instead of using browser TTS. If the file fails to
   load (broken link, 404, etc.), it automatically falls back to TTS — the
   user is never left with silence.

This second layer exists mainly for Russian words where the stress mark
(´) matters — browser TTS engines don't reliably respect stress notation,
so attaching a verified audio file for the trickier words gives a more
consistent experience.

You can set this from the "Audio File URL (optional)" field on the add-word
form at `/admin/words`. Adding audio to existing words currently requires
SQL (the admin screen doesn't have an edit feature yet):
```sql
UPDATE vocab_items SET audio_url = 'https://.../word.mp3' WHERE source_text = 'word';
```

## Deploying to Production (Railway)

This project is packaged with Docker (`Dockerfile` at the repo root) — Railway
detects and builds it automatically.

### 1. Create a project on Railway
- Go to [railway.app](https://railway.app) and create a new project by
  connecting your GitHub repo (push the repo to GitHub first if you
  haven't).
- Add a **"PostgreSQL"** service to the same project (Railway offers this
  as a one-click template).

### 2. Set environment variables
Add the following to the app service's **Variables** tab (using Railway's
cross-service reference syntax — this assumes your Postgres service is
named `Postgres`; adjust if yours has a different name):

```
SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
```

You don't need to set `PORT` yourself — Railway assigns it automatically,
and the app already reads it via `server.port=${PORT:8080}`.

> ⚠️ **The Railway UI sometimes fails to actually save variables to the
> service.** It's an undocumented quirk — the dashboard shows the variable
> as "saved", but it never reaches the running container. As a result, the
> app silently falls back to the local default in
> `application.properties` (`localhost:5432`) and you get a
> "Connection refused" error. **Always verify with the CLI after saving:**
> ```bash
> railway variables --service LangApp | grep SPRING
> ```
> If all three lines (`SPRING_DATASOURCE_URL/USERNAME/PASSWORD`) don't show
> up, don't trust the UI — set them directly from the CLI instead:
> ```bash
> railway variables --service LangApp \
>   --set 'SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}' \
>   --set 'SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}' \
>   --set 'SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}'
> ```
> Keep the `${{...}}` parts in **single quotes**, or your shell will try to
> interpret them itself. Replace `LangApp` with your actual service name.
>
> Also note: the **"Redeploy"** option in the three-dot menu reuses the
> environment variable snapshot from when that specific deployment was
> originally created — it does *not* pick up newly added variables. After
> adding/changing variables, always trigger a **fresh deploy** instead
> (e.g. `git commit --allow-empty -m "trigger redeploy" && git push`)
> rather than redeploying an old deployment.

### 3. Deploy
Railway detects the `Dockerfile` and builds/deploys automatically. On first
boot, Flyway applies the migrations (`V1`, `V2`, `V3`) to the empty
database, creating all the tables.

### 4. Load content
After deploying, you need to run `seed.sql` and the other content scripts
against Railway's Postgres service. From the project root:

```bash
railway connect Postgres
```

(Use the actual name of your Postgres service if it's different.) This
connects your local `psql` to Railway's database in an interactive session.
Once connected, run the files **in order** (order matters — `seed.sql` must
run first since the others reference the `languages`/`topics` rows it
creates):

```sql
\i src/main/resources/db/seed.sql
\i src/main/resources/db/seed_appearance.sql
\i src/main/resources/db/seed_appearance_2.sql
\i src/main/resources/db/seed_verbs.sql
\i src/main/resources/db/seed_verb_aspects.sql
```

You don't need to run `update_word_types.sql` — that one was only for
backfilling `word_type` on rows inserted before that column existed; since
Railway's database is created fresh, the other seed scripts already set it
correctly from the start.

Type `\q` to exit when done.

**Alternative (without an interactive session, one file at a time):**
```bash
railway variables --service Postgres | grep DATABASE_PUBLIC_URL
psql "<url_from_above>" -f src/main/resources/db/seed.sql
```
Note: `DATABASE_PUBLIC_URL` uses an external connection (TCP proxy), which
incurs a small network-egress cost — negligible for running a few scripts,
but prefer `railway connect` (private network) if you'll be doing this
often.

### 5. Health check (optional but recommended)
In the Railway service settings, set the **Healthcheck Path** to
`/actuator/health` — this way Railway won't route traffic to the app until
it's actually up.

### Notes
- **Installing the Railway CLI (macOS):** `brew install railway` (or
  `npm i -g @railway/cli` if you don't have Homebrew). After installing,
  run `railway login`, then `railway link` from your project folder to
  connect it.
- **Service Variables vs. Shared Variables:** This project has a single app
  service, so variables should always go in **Service Variables** (the list
  you see first under the Variables tab). Shared Variables are for cases
  where multiple services need to reuse the same value (e.g. if you add a
  worker service later) — they don't apply automatically, each service has
  to explicitly subscribe to them, so there's no need for them in a
  single-service setup.
- **Domain:** Railway gives you a free `*.up.railway.app` domain; if you
  want to attach your own domain, you can configure it under
  Settings → Networking. HTTPS is automatic.
- **Logs:** You can watch live logs from the service's "Deployments" tab in
  the Railway dashboard — the first place to check when debugging.
- **Cost:** The Hobby plan has a $5/month base fee; that's generally enough
  for an app this size plus Postgres, and your bill scales up from there if
  you go over.

## Admin — Word Management

Words can be added through the `/admin/words` screen without needing a SQL
script (the existing seed scripts are still valid for bulk-loading content
— both approaches can be used together). The form covers topic, word,
translation, example sentence, word type, verb aspect, matching verb, and
an optional audio file URL. Currently only add/delete is supported — no
editing yet.

This screen is only accessible to admin users. To make yourself an admin
(after the migrations have run, i.e. after starting the app at least once):

```sql
UPDATE users SET is_admin = true WHERE username = 'your_username';
```

You may need to log in again after running this (permissions on an
existing session aren't refreshed automatically). Once logged in, you
should see an "Admin" link in the navbar.

## Ideas for Next Steps
- Spaced repetition (SM-2 algorithm) — currently uses a simple mastery %
- Turn quizzes into an "N-question session" flow (currently shows one
  random question at a time)
- Editing existing words and creating topics from the admin screen
- Bulk Cloud TTS generation (Google Cloud TTS with SSML support for
  correct stress; a script to auto-fill `audio_url` for words that don't
  have one yet)
- A preprocessing step that strips the stress mark (´) before sending text
  to browser TTS
