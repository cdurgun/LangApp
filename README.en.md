# LangApp — English / Russian Practice App

A language-learning app skeleton built with Spring Boot 3 + Spring MVC +
Thymeleaf + Spring Security + PostgreSQL, featuring flashcards, quizzes,
and translation practice.

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
The schema is now managed by Flyway via
`src/main/resources/db/migration/V1__init.sql` — the app applies the
migration automatically on first startup. When you need a future schema
change (new column/table), don't edit the existing migration — add a new
file like `V2__description.sql` instead.

### 3. Load sample content
```bash
psql -U langapp_user -d langapp -f src/main/resources/db/seed.sql
```
This script adds a handful of English/Russian words, quiz questions, and
translation exercises. You can load your own content the same way, either
via SQL or from a CSV using `\copy`.

### 4. Run the application again
```bash
mvn spring-boot:run
```
Go to `http://localhost:8080` in your browser, register via `/register`
(pick English or Russian), log in, and start practicing.

## Project Structure
```
com.langapp
├── config       → SecurityConfig (form login, session-based auth)
├── user         → User entity, registration/login, streak tracking
├── content      → Language, Topic, VocabItem, QuizQuestion, TranslationExercise
├── progress     → UserProgress (mastery %), Attempt (attempt log)
├── practice     → PracticeService, AnswerCheckService (fuzzy match), PracticeController
└── web          → AuthController, DashboardController
```

> **Important — if you already have a local database:** If you previously
> created a local database with `ddl-auto=update`, Flyway will fail with
> "tables already exist". The easiest fix is to drop the local `langapp`
> database and start fresh (`DROP DATABASE langapp;` then re-run step 1).
> If you want to keep your existing data instead, look into the
> `flyway baseline` command.

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
boot, Flyway applies the migration (`V1__init.sql`) to the empty database,
creating all the tables.

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

## Ideas for Next Steps
- Spaced repetition (SM-2 algorithm) — currently uses a simple mastery %
- Audio pronunciation (VocabItem already has an `audio_url` field ready; a
  file/service could be wired up)
- Turn quizzes into an "N-question session" flow (currently shows one
  random question at a time)
- An admin panel for content management (currently manual SQL/CSV loading)
