# CAMINO SANCTUS — AGENT CONVENTIONS

## ABSOLUTE PROHIBITIONS
- No Firebase, Supabase, AWS SDKs, or any cloud persistence
- No REST API calls except StoreKit 2 / Play Billing receipt validation
- No AccessibilityService on Android
- No Metal GPU inference in iOS background contexts
- No AsyncTask, no GlobalScope, no runBlocking in production code
- No hardcoded strings in UI code — all user-facing text via string resources
- No comments explaining what code does. Code names self-document.

## KMP BOUNDARIES
- `shared/src/commonMain/` — 100% of business logic, state management (MVI), repositories, domain services
- `shared/src/androidMain/` and `iosMain/` — actual implementations of `expect` declarations only
- `androidApp/` and `iosApp/` — pure UI shells. Observe StateFlow from shared. No business logic.

## EXPECT / ACTUAL PATTERN
- Platform-specific APIs ALWAYS go through `expect` interfaces in commonMain
- Required `expect` declarations:
  - `VpnManager` — engageSinkhole(packageName), releaseSinkhole()
  - `SLMEngine` — load(), generate(prompt, maxTokens), unload()
  - `PermissionGate` — check(), request()
  - `PlatformDispatcher` — `expect val ioDispatcher: CoroutineDispatcher`
  - `String.normalizeForComparison()` — diacritic and case insensitive
- Never call platform APIs directly from commonMain

## CONCURRENCY
- All SQLDelight queries MUST run inside `withContext(Dispatchers.IO)`
- All ViewModels and controllers MUST accept `CoroutineScope` via constructor injection
- No `GlobalScope.launch`. No `runBlocking` outside test code.
- Cancellation must be cooperative — check `isActive` in long-running loops

## STATE MANAGEMENT (MVI)
- All UI states are sealed classes
- All sealed UI state classes MUST include an `Error` variant with `message: String` and `recoverable: Boolean`
- All intents are sealed classes
- State is exposed as `StateFlow<T>`, never `LiveData`, never raw `Flow`
- One-shot events use `SharedFlow` with replay = 0

## SERIALIZATION
- `kotlinx.serialization` for all cross-process data (iOS App Group, Android SharedPreferences, file I/O)
- JSON for App Group payloads (extension memory budget = 15MB on iOS, 6MB for some extension types)
- No Gson, no Moshi, no manual JSON

## DATABASE
- `snake_case` table and column names
- All foreign keys explicit with `REFERENCES` clauses
- Every query named in `.sq` file (no anonymous queries)
- Indexes on every column used in WHERE clauses
- Timestamps stored as `INTEGER` epoch milliseconds, never strings

## NAMING
- Kotlin classes: `PascalCase`
- Kotlin functions and variables: `camelCase`
- Constants: `SCREAMING_SNAKE_CASE`
- Compose composables: `PascalCase` matching component name
- ViewModels suffix: `ViewModel` (e.g., `EncuentroViewModel`)
- Repositories suffix: `Repository` (e.g., `ContentRepository`)
- Services suffix: `Service` (Android only) or `Manager` (cross-platform interfaces)

## ERROR HANDLING
- Repository functions return `Result<T>` or throw with typed exceptions
- ViewModels catch all exceptions and emit `Error` state — never let exceptions propagate to UI
- No silent catch blocks. Every catch either re-throws, logs, or emits state.

## TESTING
- Every domain class in `shared/commonMain` has a corresponding test in `shared/commonTest`
- Repository tests use in-memory SQLDelight driver
- ViewModel tests use `TestCoroutineDispatcher`
- No integration tests against real platform APIs in shared module
