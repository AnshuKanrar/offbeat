# Offbeat — TODO (bottom → top)

Scope of this list: **get your 20 songs from Navidrome into the app's database and onto the screen.**
Playback, downloads, stats, etc. come later (see `docs/ARCHITECTURE.md` §9.2).

How to use this file:
- Work top to bottom. Each step needs only the steps above it.
- Each step says **Learn** (what to read/watch), **Build** (files to create), **Done when** (how you know it works).
- `§N` = section in `docs/ARCHITECTURE.md`. Read that section *when you reach the step*, not before.
- Commit after every step. The architecture doc is 2800 lines. You are not expected to read it all.

Package note: sources go in `app/src/main/java/dev/yourname/offbeat/` (§2.3). Replace `yourname` with your own name.

---

## The big picture (read once, 5 min)

Data flows **bottom → top**. You build in this same order:

```
Navidrome (server, your HDD)
   ↓  HTTP + JSON
[1] network/   — talks to the server           (Retrofit, DTOs)
   ↓
[2] database/  — stores a copy on the phone     (Room: entities, DAOs)
   ↓
[3] data/      — Repository: network → database (sync), database → UI
   ↓
[4] ViewModel  — holds screen state             (StateFlow)
   ↓
[5] ui/        — Compose screen shows it
```

The UI only ever reads from the database (§1.4 "offline-first"). The network only ever writes to it.

---

## Phase 0 — Prepare (about 1 hour)

- [x] **0.1 Navidrome running and reachable**
  - Install Navidrome on the machine with the HDD. Point `MusicFolder` at the 20 songs. Make sure songs have tags (title, artist, album).
  - Open `http://<computer-LAN-IP>:4533` from your **phone's browser** on the same Wi-Fi. Create the admin user.
  - Note down: URL, username, password.
  - Done when: you see your 20 songs in the Navidrome web page, from the phone.
- [x] **0.2 Test the API by hand** (no code yet). In a browser, open:
  `http://<IP>:4533/rest/ping?u=USER&p=PASSWORD&v=1.16.1&c=offbeat&f=json`
  Then `.../rest/getAlbumList2?type=alphabeticalByName&u=...&p=...&v=1.16.1&c=offbeat&f=json`
  - Learn: what JSON looks like; this is exactly what your app will receive (§7.1, §7.5).
  - Done when: you see `"status":"ok"` and your albums in the JSON.
  - (The app will use token+salt auth instead of `p=`; `p=` is just for this manual test.)
- [ ] **0.3 `git init`** and commit the template project. (§0.2)
- [ ] **0.4 Rename package** `com.example.offbeat` → `dev.yourname.offbeat` (namespace, applicationId, move source folders: right-click package in Android Studio → Refactor → Rename). Do this **before anything else** (§0.2).

---

## Phase 1 — Learn the Kotlin you need (1–2 days, in this order)

You don't need to master these. Learn enough to read the code you'll write.

1. **Kotlin basics**: `val`/`var`, `data class`, nullable `?`, `sealed class`, lambdas, `when`. (kotlinlang.org/docs → "Kotlin Koans"/"Learn Kotlin by example")
2. **Coroutines + Flow**: `suspend fun`, `viewModelScope.launch`, `Flow`, `StateFlow`. This is the most important concept in the whole app. (developer.android.com → "Kotlin coroutines on Android", "Kotlin flows on Android") — see also §1.6.
3. **JSON in Kotlin**: `kotlinx.serialization` (`@Serializable`).
4. **Gradle version catalog**: how `gradle/libs.versions.toml` feeds `app/build.gradle.kts` (you already have both files).

Done when: you can explain what `suspend` means and what `Flow<List<Track>>` emits.

---

## Phase 2 — Build setup (about 1 hour)

- [ ] **2.1 Add dependencies** to `gradle/libs.versions.toml` and `app/build.gradle.kts` (§8.6 has the full lists). For this TODO you need only:
  - Plugins: `kotlin.plugin.serialization`, `ksp`, `androidx.room`, `hilt`
  - Libraries: Retrofit, OkHttp (+ logging interceptor), `kotlinx-serialization-json`, Retrofit's kotlinx converter, Room (`runtime`, `ktx`, `compiler`), Hilt (`android`, `compiler`), lifecycle (`viewmodel-compose`, `runtime-compose`)
  - Bump stale template versions (lifecycle 2.6.1, activity-compose 1.8.0) as §0.2 says.
  - Watch for: AGP 9 has built-in Kotlin — do **not** apply `org.jetbrains.kotlin.android` (§0.2, §10).
  - Done when: **Sync Project** succeeds and the app still launches.
- [ ] **2.2 Manifest + cleartext**: add `<uses-permission android:name="android.permission.INTERNET"/>`. Your Navidrome is `http://`, which Android blocks by default → add a network security config (§8.3, §8.4). Android 17 may also need the **local network permission** (§8.4).
  - Done when: manifest compiles; you know why `http://` needs a config.

---

## Phase 3 — Models (Layer 0: no Android, no libraries)

- [ ] **3.1 Pure Kotlin models** in `core/model/` (§2.3). Start with only these:
  - `Track.kt`, `Album.kt`, `Artist.kt`, `ServerConfig.kt`, `AppError.kt` (this one lives in `core/common/`)
  - Plain `data class`es. No `@Entity`, no `@Serializable` here. These are what the UI will see.
  - Learn: why 3 versions of "Track" exist: DTO (network), Entity (database), Model (UI) — §0.3 "Domain layer", §1.1.
  - Done when: files compile. Write a tiny unit test that builds a `Track` (optional).

---

## Phase 4 — Network: talk to Navidrome (Layer 1)

Folder: `core/network/` (§2.3, §7)

- [ ] **4.1 Auth helper** — `network/auth/TokenAuth.kt`
  - `newSalt()` and `token(password, salt)` = md5(password+salt) (§7.1, code is in the doc).
  - Learn: why the password is never sent or stored.
  - Done when: a unit test checks `token("sesame", "c19b2d")` = `26719a1196d2a940705a59634eb18eab` (the Subsonic docs' example).
- [ ] **4.2 DTOs** (the JSON shapes) — `network/dto/EnvelopeDto.kt`, `LibraryDtos.kt`
  - `@Serializable` data classes matching what you saw in step 0.2. Start with: envelope (`subsonic-response`, `status`, `error`), `AlbumID3Dto`, `ChildDto` (song), `AlbumWithSongsDto`, `ArtistID3Dto`.
  - Make unknown fields ignorable: `Json { ignoreUnknownKeys = true }`.
  - Done when: a unit test parses a JSON string you copied from the browser in 0.2.
- [ ] **4.3 Retrofit interface** — `network/SubsonicApi.kt`
  - One `suspend fun` per endpoint. Start with just: `ping`, `getAlbumList2`, `getAlbum`, `getArtists` (§7.5).
- [ ] **4.4 Interceptors** — `network/auth/AuthInterceptor.kt`, `network/interceptor/ServerUrlInterceptor.kt`
  - AuthInterceptor adds `u`, `t`, `s`, `v`, `c`, `f` to every request (§7.1).
  - ServerUrlInterceptor swaps a placeholder base URL for the user's real URL at runtime (§7.3).
  - Learn: what an OkHttp interceptor is.
- [ ] **4.5 Safe calls + errors** — `network/ApiCall.kt`
  - `safeApiCall { }` returns `Result<T>` and maps failures to `AppError` (§7.4). Subsonic returns HTTP 200 even for errors — check `status`.
- [ ] **4.6 Hilt: first taste** — `OffbeatApp.kt` (`@HiltAndroidApp`), `MainActivity` (`@AndroidEntryPoint`), and `network/di/NetworkModule.kt` providing `OkHttpClient`, `Retrofit`, `SubsonicApi` (§6.1–§6.3).
  - Learn: what dependency injection solves (one paragraph is enough for now).
- [ ] **4.7 Throwaway test screen**: in `MainActivity`, temporarily call `ping` + `getAlbumList2` from a `LaunchedEffect` and `Log.d` the result. Hardcode your URL/user/password for now (you'll delete this).
  - Done when: **Logcat shows your 20 songs' albums coming from your phone → Navidrome.**
  - Emulator tip: the emulator can't use your LAN IP the same way; use a real phone, or `10.0.2.2` if the server is on the same Mac (§7.7).

**Milestone A: the app can talk to Navidrome.**

---

## Phase 5 — Room: store it on the phone (Layer 2)

Folder: `core/database/` (§2.3, §4)

- [ ] **5.1 Learn Room**: Entity, DAO, Database, `Flow` return types, `@Upsert`, `@Transaction`. (developer.android.com → "Save data in a local database using Room")
- [ ] **5.2 Entities** — `database/entity/LibraryEntities.kt`
  - `ArtistEntity`, `AlbumEntity`, `TrackEntity` (code sketch in §4.2). **Start with a trimmed version** (id, name, artist, album, duration, coverArtId, syncRunId). Add the rest when you need them — but add before you ship, since later changes need migrations (§4.9).
- [ ] **5.3 DAOs** — `database/dao/ArtistDao.kt`, `AlbumDao.kt`, `TrackDao.kt` (§4.4)
  - Reads return `Flow<List<…>>`. Writes are `@Upsert`. Add a "sweep" delete: `DELETE … WHERE syncRunId < :runId` (this removes things deleted on the server).
- [ ] **5.4 Database class** — `OffbeatDatabase.kt`, `Converters.kt` (§4.1, §2.3), `exportSchema = true`.
- [ ] **5.5 Hilt module** — `database/di/DatabaseModule.kt` provides the database and each DAO.
- [ ] **5.6 Mappers** — Entity ↔ Model, DTO → Entity (e.g. `ChildDto.toEntity(runId)`, `TrackEntity.toModel()`), kept next to the entities/DTOs.
- [ ] **5.7 Test** — an `androidTest` using an in-memory Room DB: insert 3 tracks, read them back, sweep one (§8.7).
  - Done when: the test passes.
  - Debug aid: Android Studio → **App Inspection → Database Inspector** lets you see the tables live.

**Milestone B: you can save and read tracks locally.**

---

## Phase 6 — Repository + sync: network → database (Layer 3)

Folder: `data/` (§2.3 "data" section; §7.6 is the algorithm)

- [ ] **6.1 Server config store** — `ServerConfigStore` (DataStore) holding URL, username, salt, token.
  - Credentials encrypted with an Android Keystore AES-GCM key (§7.2). **For a first working version**, plain DataStore is OK on your own phone — but mark it `// TODO encrypt` and finish before sharing the app.
- [ ] **6.2 `ServerRepository.signIn(url, username, password)`** (§7.1): make salt+token → `ping` → on OK save config, **throw away the password**.
- [ ] **6.3 `LibrarySyncer`** — the simple version of §7.6:
  1. `runId = now()`
  2. `getAlbumList2` in pages of 500 → upsert albums
  3. for each album → `getAlbum(id)` → upsert tracks
  4. `getArtists` → upsert
  5. sweep rows with `syncRunId < runId`
  - With 20 songs, skip the clever parts (changeKey, 4-at-a-time semaphore, 50% safety check). Add them when your library grows. Leave a `// ponytail:` style comment where you skipped them.
- [ ] **6.4 `LibraryRepository`** — exposes `Flow<List<Album>>`, `Flow<List<Track>>` from the DAOs (mapped to models) plus `suspend fun sync()`.
- [ ] **6.5 Hilt wiring** for repositories (concrete `@Inject constructor` classes, no interfaces — §0.3).
- [ ] **6.6 (Later, not now)** `LibrarySyncWorker` + `SyncScheduler` with WorkManager for background sync (§7.6, block 3 in §9.1). Manual "Sync" button first.
  - Done when: calling `sync()` from your test screen fills Database Inspector with 20 tracks. **Turn on airplane mode, restart the app: the data is still there.**

**Milestone C: offline-first data layer works.**

---

## Phase 7 — ViewModel + minimal UI: see it on screen (Layers 4–5)

Folders: `ui/setup/`, `ui/library/` (or per §2.3 feature folders). First learn: Compose basics (`@Composable`, `remember`, `LazyColumn`, state hoisting), and the ViewModel/UDF pattern (§1.5).

- [ ] **7.1 Learn Compose** (developer.android.com → "Jetpack Compose basics" codelab) — do before writing UI.
- [ ] **7.2 Setup screen** — `SetupScreen.kt` + `SetupViewModel.kt` (§7.7)
  - Fields: URL, username, password. Button "Connect" → `ServerRepository.signIn`.
  - Show states: idle / checking / error ("Can't reach server" vs "Wrong credentials") / success.
  - Pattern: `UiState` data class + `StateFlow` in ViewModel, `collectAsStateWithLifecycle()` in the screen (§1.5).
  - Remove the hardcoded credentials from step 4.7.
- [ ] **7.3 Sync trigger**: after sign-in succeeds, run `LibraryRepository.sync()` and show a progress/"Done" state.
- [ ] **7.4 Album list screen** — `AlbumListScreen.kt` + ViewModel reading `Flow<List<Album>>` from Room into a `LazyColumn` (title, artist, song count).
- [ ] **7.5 `AppRoot.kt`**: if a server is configured show the album list, else show Setup (§5.7).
  - Done when: fresh install → enter URL/user/password → songs appear → airplane mode → still appear.

**Milestone D: Navidrome → app → Room → screen. This is the "spine" of the project.**

---

## After this (not part of this TODO)

Follow `docs/ARCHITECTURE.md` §9.2, in this order: Navigation 3 shell (§5) → **Playback with Media3 (§3)** → album/playlist detail → likes → search → queue → server folders → downloads → … The big next step is playback; learn Media3 before starting it (§3.1–§3.5).

---

## Where am I? (file map for this TODO)

```
app/src/main/java/dev/yourname/offbeat/
├── OffbeatApp.kt                     ← 4.6   (@HiltAndroidApp)
├── MainActivity.kt                   ← 4.6, 4.7 (throwaway test), later 7.5
├── core/
│   ├── model/        Track, Album, Artist, ServerConfig   ← Phase 3
│   ├── common/       AppError                              ← Phase 3
│   ├── network/      TokenAuth, dto/, SubsonicApi, interceptors, ApiCall, di/   ← Phase 4
│   └── database/     entity/, dao/, OffbeatDatabase, di/   ← Phase 5
├── data/             ServerConfigStore, ServerRepository, LibrarySyncer, LibraryRepository   ← Phase 6
└── ui/ (or feature folders)   SetupScreen/ViewModel, AlbumListScreen/ViewModel, AppRoot   ← Phase 7

gradle/libs.versions.toml, app/build.gradle.kts   ← 2.1
app/src/main/AndroidManifest.xml + res/xml/network_security_config.xml   ← 2.2
```

Everything else in §2.3 (navigation, designsystem, playback, downloads, stats, …) stays **unbuilt for now**.

## Stuck? Common first-timer problems

- "Can't reach server" on the phone → server and phone on different networks, firewall blocking port 4533, or cleartext not allowed (2.2).
- Gradle sync fails after adding Hilt/KSP → AGP 9 plugin compatibility, see §0.2 and §10.
- JSON parse crash → missing `ignoreUnknownKeys = true`, or a field you marked non-null is absent in the response (make it nullable).
- Songs vanish after sync → your sweep deleted rows because `syncRunId` wasn't updated on every upsert (§7.6 step 2 warning).
