# CAMINO SANCTUS — MASTER EXECUTION FRAMEWORK

**Version:** 1.0 — LOCKED
**Status:** Approved. Build-ready.
**Owner:** Lead Architect (human)
**Purpose:** Single governing document for the entire Camino Sanctus build. Prepended to every agent session. Referenced before every architectural decision. Updated only with explicit human re-approval.

---

## TABLE OF CONTENTS

1. Project Constitution (Locked Decisions)
2. Agent Conventions (CONVENTIONS.md — prepended to every session)
3. System Architecture
4. UX Doctrine (Umbral Sagrado)
5. Risk Registry
6. Phased Execution Plan
7. Agent Orchestration Protocol
8. Quality Gates
9. Operational Protocols
10. Appendix A — Corrected InterceptController reference
11. Appendix B — Permission onboarding copy
12. Appendix C — Apple FamilyControls entitlement framing

---

## 1. PROJECT CONSTITUTION (LOCKED DECISIONS)

These decisions are immutable. Agents do not deviate. Human re-approval required to modify any item in this section.

### 1.1 Framework
- **Kotlin Multiplatform (KMP)** for shared business logic only
- **Jetpack Compose** for Android UI
- **SwiftUI** for iOS UI
- **No Flutter. No React Native. No web views for core UX.**

### 1.2 Persistence
- **SQLDelight + SQLite** for all relational data, fully local
- **No Firebase. No Supabase. No AWS. No REST APIs** except App Store / Play Store purchase validation
- Four schemas: `content_cards`, `intercept_log`, `user_profile`, `umbral_state`
- Database package: `com.panoplia.caminosanctus.db`

### 1.3 Dependency Injection
- **Koin only**. No Dagger. No Hilt.
- Scope strategy: lifecycle-aware `CoroutineScope` injected into all controllers and ViewModels

### 1.4 Android Intercept Stack
- `UsageStatsManager` polling inside foreground coroutine scope
- `AppMonitorService` declared as `FOREGROUND_SERVICE_TYPE_SPECIAL_USE` with persistent silent notification
- `BlockingVpnService` null-routes target app traffic to `10.0.0.1`
- `SYSTEM_ALERT_WINDOW` overlay masks the 300–800ms polling latency
- **`AccessibilityService` is banned.** Android 17 AAPM restricts it to genuine assistive tools only.
- `WorkManager` only for non-real-time state sync (minimum 15-minute interval)

### 1.5 iOS Intercept Stack
- `DeviceActivityMonitor` extension monitors selected apps
- `ShieldConfiguration` extension renders the intercept UI
- `ShieldAction` extension handles button interactions
- App Group shared container (`group.com.panoplia.caminosanctus`) carries a 20-card preload queue between main app and extensions
- **Focus Filters fallback track exists from week 1** — iOS ships regardless of FamilyControls outcome

### 1.6 AI Inference
- **Model:** Qwen3-0.6B quantized to Q4_K_M (~400MB)
- **Android:** `llama.cpp` JNI bindings, memory-mapped loading, foreground only
- **iOS background:** Core ML, Apple Neural Engine (ANE) targeted, never Metal GPU
- **iOS foreground:** Core ML or Metal-backed inference acceptable
- **Device capability gate:** RAM check before model load. Below 4GB → fallback to cloud (Gemini Flash or Kimi API), not on-device
- **No on-device SLM in v1 for users on devices below the RAM threshold.** Cloud is an acceptable degraded path.

### 1.7 UX — Umbral Sagrado
Three-phase escalation. Order is fixed. Triggers are based on bypass count in rolling 60-minute window and current streak.

- **Phase 1 — Encuentro:** 15-second soft intercept. No close button. Progress bar fills, then "Continuar" appears.
- **Phase 2 — Disciplina:** Triggered after 3 bypasses in 60 minutes.
  - **Default interaction:** 30-second guided breathing (3 cycles with animated visual)
  - **Alternative (user-configurable):** single-tap theological reflection
  - **Optional (user-enabled, never default):** typing a custom phrase
  - **Accessibility path:** users with motor or cognitive accessibility needs default to the breathing or tap mode
- **Phase 3 — Profundidad:** Hard lock. Shield UI. "Hablar con Clemente" routes to SLM chat.
  - **Emergency bypass:** 5-second button hold (Opal-style). Bypass logged to `intercept_log`. Streak is NOT broken. No shaming UI.
  - **Lock duration:** 15 minutes default, user-configurable down to 5 minutes minimum

### 1.8 Monetization
- **Emotional peak paywall.** Never shown on sessions 1–3. Triggered by: post-share, post-favorite, post-Phase-3-completion, 45s+ session duration, end of free reflection.
- **Pricing tiers** (territory-localized via App Store Connect / Play Console):
  - **Tier 1 (Core LatAm):** $1.99/mo, $14.99/yr equivalent in local currency
  - **Tier 2 (Diaspora + Spain):** $4.99/mo, $29.99/yr equivalent
  - **Argentina:** weekly subscription model due to inflation volatility
- **Free trial:** 3 days on monthly, 7 days on annual
- **Lifetime tier:** Available only as win-back offer or gifting flow, not on standard paywall

### 1.9 Design Language — Monastic Minimalism
- **Backgrounds:** Umbral Onyx `#0A0A14`
- **Primary accent:** Byzantine Gold `#D4AF37`
- **Secondary text:** Incense Ash `#8B8B99`
- **Warning / Hard Lock:** Martyr Red `#7A1A1A`
- **Typography:** Crimson Pro (serif, scripture and headers) + Inter (sans, system and buttons)
- **No white backgrounds. No calming pastel blues. No rounded "friendly" iconography.**

---

## 2. AGENT CONVENTIONS — `CONVENTIONS.md`

This section is prepended verbatim to every agent session. It is the binding instruction set for all autonomous code generation.

```markdown
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
```

---

## 3. SYSTEM ARCHITECTURE

### 3.1 Module Topology

```
camino-sanctus/
├── settings.gradle.kts
├── gradle/libs.versions.toml
├── build.gradle.kts
├── shared/                              KMP shared module
│   ├── src/commonMain/kotlin/com/panoplia/caminosanctus/
│   │   ├── intercept/                   InterceptController, UmbralState, InterceptIntent
│   │   ├── domain/                      PhaseEngine, StreakCalculator, PaywallGate, ContentRanker
│   │   ├── data/                        Repositories, mappers
│   │   ├── di/                          Koin modules
│   │   └── platform/                    expect declarations
│   ├── src/commonMain/sqldelight/com/panoplia/caminosanctus/db/
│   │   ├── ContentCards.sq
│   │   ├── InterceptLog.sq
│   │   ├── UserProfile.sq
│   │   └── UmbralState.sq
│   ├── src/androidMain/                 actual implementations: VpnManager, SLMEngine, etc.
│   ├── src/iosMain/                     actual implementations
│   └── src/commonTest/
├── androidApp/
│   ├── src/main/kotlin/com/panoplia/caminosanctus/android/
│   │   ├── ui/                          Compose screens (3 Umbral phases + main)
│   │   ├── service/                     AppMonitorService, BlockingVpnService
│   │   ├── overlay/                     OverlayManager (SYSTEM_ALERT_WINDOW)
│   │   ├── permission/                  Onboarding flow
│   │   └── billing/                     Play Billing integration
│   ├── src/main/AndroidManifest.xml
│   └── build.gradle.kts
├── iosApp/
│   ├── CaminoSanctus/                   Main app target (SwiftUI)
│   ├── ShieldConfiguration/             Extension target
│   ├── ShieldAction/                    Extension target
│   ├── DeviceActivityMonitor/           Extension target
│   └── Shared/                          App Group shared types
├── .github/workflows/
│   └── ios-build.yml                    macOS-15 runner
└── content/
    └── seed_database.json               300+ Panoplia cards
```

### 3.2 Data Flow

User installs app → onboarding requests permissions → user selects monitored apps → `AppMonitorService` (Android) / `DeviceActivityMonitor` (iOS) tracks target app launches → `InterceptController` queries `PhaseEngine` → `PhaseEngine` reads `intercept_log` for rolling 60-minute bypass count + `user_profile` for streak → determines phase → returns `UmbralState` → platform UI renders the appropriate screen → user completes interaction → unlock event logged → VPN sinkhole released (Android) / shield removed (iOS) → target app accessible.

### 3.3 Cross-Process Boundaries

**Android:** Main app process and `BlockingVpnService` share state via SQLDelight (single source of truth). `AppMonitorService` and overlay communicate via in-process state holder.

**iOS:** Main app and extensions are separate processes with separate memory. Synchronization via App Group `UserDefaults` (small state) + shared `FileManager` container (preloaded content queue as JSON). Main app refreshes the queue on every foreground transition.

---

## 4. UX DOCTRINE — UMBRAL SAGRADO

### 4.1 Phase Triggering Logic

```
function determinePhase(packageName, userState):
    bypassCount = count(intercept_log WHERE target_app = packageName
                        AND timestamp > now - 60_minutes
                        AND completed = false)

    if userState.subscriptionTier == NONE and userState.daysInstalled < 4:
        return ENCUENTRO  // soft only during free grace period

    if bypassCount >= 5:
        return PROFUNDIDAD
    if bypassCount >= 3:
        return DISCIPLINA
    return ENCUENTRO
```

### 4.2 Phase 1 — Encuentro

Full-screen overlay. Umbral Onyx background. Centered scripture in Crimson Pro. Linear progress bar fills from Incense Ash to Byzantine Gold over 15 seconds. After 15 seconds, "Continuar" button fades in. Tapping it releases the VPN sinkhole and dismisses the overlay.

No close button. No skip. No paywall surface. This is the gift phase.

### 4.3 Phase 2 — Disciplina

Three interaction modes. User selects preferred mode in settings during onboarding. Breathing is default.

**Mode A — Guided Breathing (DEFAULT)**
30 seconds. Three breath cycles (inhale 4s, hold 2s, exhale 4s). Animated circle expands and contracts. Verse displayed below the visual. After three cycles, gentle haptic confirmation, unlock available.

**Mode B — Tap Reflection**
Single-question theological reflection drawn from `content_cards`. User taps one of three answers. Answer is logged. Brief personalized response appears (1–2 sentences). Unlock available.

**Mode C — Typing (OPT-IN ONLY)**
Never default. Must be explicitly enabled in settings. User types a phrase from a small library or a custom phrase they set themselves. String comparison is diacritic-insensitive, case-insensitive, whitespace-tolerant (`String.normalizeForComparison()` in commonMain).

**Accessibility:** Users with motor or cognitive accessibility settings detected on the device default to Mode A. Mode C is never auto-suggested to such users.

### 4.4 Phase 3 — Profundidad

Hard shield. Martyr Red accent on Byzantine Gold shield icon. Text: "Acceso bloqueado. Tiempo de oración."

Two primary actions:
- **"Hablar con Clemente"** — routes to the SLM chat companion. Bounded 5-turn conversation. After conversation, all monitored apps unlock for 30 minutes.
- **"Esperar"** — countdown visible. Lock duration is user-configurable (5–30 minutes, default 15).

**Emergency bypass:**
- Small "Emergencia" link at bottom of screen
- Tapping opens a confirmation: "¿Necesitas acceso inmediato? Mantén presionado 5 segundos."
- 5-second hold required (prevents accidental triggers)
- Bypass granted, logged to `intercept_log` with `bypass_reason = EMERGENCY`
- Streak is NOT broken. No shaming UI. No "you failed" copy.
- Soft note: "Tu camino continúa. La próxima pausa te espera."

This pattern is verified in Opal, One Sec, and Jomo. App Store and Play Store accept it.

---

## 5. RISK REGISTRY

| ID | Risk | Severity | Mitigation | Status |
|----|------|----------|------------|--------|
| R1 | Apple FamilyControls denial | Critical | File Day 1. Focus Filters fallback in parallel. iOS ships either way. | Open — file Monday |
| R2 | Android FGS battery / kill | High | `FOREGROUND_SERVICE_TYPE_SPECIAL_USE` + persistent notification. Polling on Dispatchers.Default not Main. | Mitigated in conventions |
| R3 | Google Play VPN scrutiny | High | "Digital Wellbeing" framing. Data Safety form prepped. Privacy Policy explicit on local-only null route. | Open — draft before submission |
| R4 | SLM OOM on low-RAM devices | High | RAM check ≥4GB before model load. Cloud fallback below threshold. Qwen3-0.6B Q4_K_M cap. | Mitigated |
| R5 | Main-thread DB crash | Critical | Dispatchers.IO enforced in conventions. Every agent session prepended. | Mitigated |
| R6 | Coroutine scope leak | High | CoroutineScope injected via Koin. Lifecycle-aware. | Mitigated |
| R7 | Phase 2 coercion / accessibility | Medium | Typing opt-in only. Breathing default. Accessibility detection. | Approved — locked |
| R8 | Phase 3 rage-uninstall | Medium | Opal-style emergency bypass. Logged not penalized. | Approved — locked |
| R9 | Agent drift on large codebase | Medium | CONVENTIONS prepended every session. Weekly coherence review. | Process control |
| R10 | iOS CI compile latency | Medium | Batch iOS changes. Architectural decisions before coding. | Accepted |
| R11 | Theological content accuracy | High | Human review gate before every content batch. No agent writes final Spanish. | Process control |
| R12 | Hallucinated APIs / models | Medium | Locked stack. Kimi for verification passes. Claude Code for OS-level files. | Mitigated by orchestration |
| R13 | Brittle string comparison in Phase 2 typing | High | `normalizeForComparison()` in commonMain — strips diacritics, lowercases, trims. | Mitigated in conventions |
| R14 | Hard 15-min lock without override | High | Emergency bypass approved. User-configurable duration. | Mitigated |

---

## 6. PHASED EXECUTION PLAN

Each phase has a hard gate. No phase proceeds without its gate passing.

### Phase 0 — Foundation (Weeks 1–2)

**Parallel track activated Day 1:** Apple FamilyControls entitlement filed (see Appendix C for framing). Clock runs through all phases.

**Agent work:**
- Antigravity: Generate `settings.gradle.kts`, `gradle/libs.versions.toml`, three module scaffolds, GitHub Actions iOS workflow, basic Koin modules, expect/actual interface stubs
- Claude Code: SQLDelight schemas — four `.sq` files with full table definitions, indexes, foreign keys, named queries
- Kimi API: Ingest Panoplia source material, generate 300 structured content cards in seed JSON format
- Human: Theological accuracy review of all 300 cards. Approve or correct.

**Gate 0 acceptance criteria:**
- `./gradlew build` passes on Windows
- iOS workflow compiles empty targets on macOS-15 runner
- All four SQLDelight schemas compile and generate Kotlin
- 300 cards present in `seed_database.json`, all human-approved
- CONVENTIONS.md complete and added to repo root

### Phase 1 — Shared Business Logic (Weeks 3–4)

**Agent work:**
- Antigravity: Build `InterceptController` (use Appendix A as reference template), `PhaseEngine`, `StreakCalculator`, `PaywallGate`, `ContentRanker`
- Claude Code: Repository implementations against SQLDelight, Koin wiring, expect/actual implementations for `PlatformDispatcher`
- Kimi API: Generate unit test suites for all domain logic
- Human: Verify state machine transitions, review test coverage

**Gate 1 acceptance criteria:**
- All unit tests pass
- State transitions verified: `Idle → SoftIntercept → EngagedIntercept → FullLock → Idle`
- `PhaseEngine.determinePhase()` produces correct phase for given bypass history inputs
- `PaywallGate.shouldShow()` returns false on sessions 1–3, returns true only on emotional peak signals
- Repository tests pass against in-memory SQLDelight driver

### Phase 2 — Android Platform (Weeks 5–10)

**Agent work:**
- Antigravity: Compose UI screens (Encuentro, Disciplina with all three modes, Profundidad with emergency bypass, main app shell, settings, onboarding)
- Claude Code: `AppMonitorService`, `BlockingVpnService`, `OverlayManager`, `BootReceiver`, AndroidManifest.xml, llama.cpp JNI integration
- Kimi API: 22-territory pricing arrays formatted for Play Console, all Spanish UI strings reviewed for tone
- Human: Real-device testing on Android 14+ throughout — minimum one $80-tier device, one mid-range, one flagship

**Gate 2 acceptance criteria:**
- App intercepts a target app (e.g., Instagram) on a real Android 14+ device
- All three Umbral phases trigger correctly based on bypass history
- VPN sinkhole engages during Phase 3
- Emergency bypass works and logs to `intercept_log` with correct reason
- Play Billing sandbox purchase completes for monthly and annual
- App survives device reboot — services restart correctly via `BootReceiver`
- Battery drain measured ≤2% per 24 hours of normal usage

### Phase 3 — iOS Platform (Weeks 11–16)

Two parallel tracks until FamilyControls verdict.

**Track A (FamilyControls granted):**
- `DeviceActivityMonitor` extension
- `ShieldConfiguration` extension reading from App Group queue
- `ShieldAction` extension handling button taps
- Main app preload pipeline keeping 20-card queue fresh

**Track B (Focus Filters fallback, runs regardless):**
- Focus Filter integration with system Focus modes
- Lock screen widget (WidgetKit) — daily Panoplia verse
- Notification-based intercept using Screen Time aggregate data

**Common to both tracks:**
- SwiftUI shells consuming KMP shared ViewModels via Kotlin/Native framework
- Core ML model conversion via `coremltools`, ANE-targeted
- StoreKit 2 with same territory pricing as Android

**Gate 3 acceptance criteria:**
- Intercept fires on iOS 17+ real device (via TestFlight build)
- App Group data correctly shared between main app and all extensions
- StoreKit 2 sandbox purchase completes
- Core ML inference runs in background without `IOGPUMetalError`
- Focus Filters fallback fully functional independent of FamilyControls
- Extension memory stays under 15MB during shield rendering

### Phase 4 — Polish to Highest Standards (Weeks 17–19)

**Performance:**
- Cold start ≤1 second on mid-range device
- Battery drain ≤2% per 24 hours
- SQLDelight query profile with `EXPLAIN QUERY PLAN`
- Compose recomposition audit using Layout Inspector

**Accessibility:**
- Dynamic Type / Font Scaling supported on all text
- TalkBack (Android) and VoiceOver (iOS) labels on every interactive element
- Contrast ratio ≥4.5:1 verified across all screens (WCAG AA)

**Animation:**
- Phase transitions choreographed with `AnimatedVisibility` / matched geometry
- Breathing animation in Disciplina runs at 60fps
- Shield appearance feels heavy and ceremonial — no bouncy spring animations

**Content & Copy:**
- Every user-facing Spanish string reviewed by human for voice and cultural fit
- No agent writes final Spanish copy
- Localized App Store / Play Store assets for MX, ES, CO, US (Spanish-language listing), AR — minimum five territory sets

**Privacy & Legal:**
- iOS Privacy Manifest complete
- Android Data Safety form complete
- Privacy Policy published at accessible URL
- Terms of Service published at accessible URL

**Gate 4 acceptance criteria:**
- Cold start measured <1s on a mid-range LatAm device
- Zero crashes in 48-hour soak test on both platforms
- WCAG AA contrast verified across all screens
- All animations measured at 60fps with no frame drops
- Store assets approved by human

### Phase 5 — Submission (Weeks 20–22)

**Android:** Internal Testing → Closed Testing (10–20 users) → Production submission
**iOS:** TestFlight Internal → TestFlight External (up to 10K) → App Store submission

Both stores: "Digital Wellbeing" category. Privacy Policy linked. Data Safety / Privacy Manifest complete.

**Budget one rejection cycle per platform.** Plan one week per rejection + resubmission.

**Gate 5 acceptance criteria:**
- Live on both stores
- Both subscription products purchasable in at least five territories per platform
- Crash-free user rate ≥99% in first 48 hours
- Both stores show app correctly localized in all five primary territories

---

## 7. AGENT ORCHESTRATION PROTOCOL

### 7.1 Tool Assignment Matrix

| Task category | Primary tool | Why |
|---|---|---|
| Autonomous feature builds (3–6h sessions) | **Antigravity (Gemini 3.x Pro)** | Multi-file autonomous generation; best at producing whole features end-to-end |
| OS-level services & extensions | **Claude Code** | Highest accuracy on Kotlin/Swift platform APIs; lowest hallucination on `BlockingVpnService`, `DeviceActivityMonitor`, `ShieldConfiguration` |
| Surgical crash debugging | **Claude Code** | Best at reading stack traces and fixing root causes |
| Architectural decisions & red team | **Claude (web/desktop)** | This conversation. Use for "should we…?" and "review what the agent just did" |
| Content card generation from Panoplia | **Kimi API** | 128K context handles large theological source docs in one shot; near-zero cost per batch |
| Long-context codebase review | **Kimi API** | Cheap to read the whole repo at once and flag drift |
| Localization & translation (UI strings) | **Kimi API** | Strong multilingual; cheap at scale |
| Boilerplate generation (manifests, configs, schemas) | **Kimi API** | Cheap, correctness easy to verify |
| Bulk format conversion / shell tasks | **Gemini CLI** | Already in terminal; handles file system operations well |
| Real-device testing | **Human (you)** | Non-delegable |
| Theological content review | **Human (you)** | Non-delegable |
| Final Spanish copy review | **Human (you)** | Non-delegable |
| Design direction | **Human (you)** | Non-delegable |

### 7.2 Session Preparation Protocol

Before every agent session:
1. Prepend this MASTER_FRAMEWORK.md (or at minimum Sections 1, 2, and the current phase from Section 6)
2. State the specific phase and gate criteria the session is working toward
3. Provide concrete acceptance criteria as a checklist
4. Identify which files the agent may modify and which are off-limits

After every agent session:
1. Review all diffs before merging — no auto-merge ever
2. Run the build locally (Windows) for Android; trigger CI for iOS
3. Update the project log with what was completed and what's pending
4. If the session introduced architectural decisions not in this framework, escalate to a Claude (web) review before merging

### 7.3 When Agents Disagree With This Framework

Agents will occasionally suggest "better" approaches that contradict this document. The protocol:

1. Default: this document wins. Reject the suggestion.
2. If the suggestion seems genuinely better, escalate to Claude (web) for red team review with full context
3. Human approves the change explicitly before any code is modified
4. This document is updated with a version bump
5. New conventions propagated to next agent session

---

## 8. QUALITY GATES — DEFINITION OF DONE

The app ships only when ALL of the following are true:

**Functional:**
- All three Umbral phases functional on Android 14+ and iOS 17+
- Emergency bypass works on both platforms
- Paywall appears only on emotional peak signals, never sessions 1–3
- StoreKit 2 and Play Billing both process sandbox purchases in at least 5 territories
- App survives device reboot on both platforms
- VPN sinkhole engages and releases correctly on Android
- iOS extensions stay under 15MB memory during shield render

**Performance:**
- Cold start ≤1 second on mid-range device
- Battery drain ≤2% per 24 hours
- Zero crashes in 48-hour soak test
- Crash-free rate ≥99% in first 48 hours post-launch
- All animations at 60fps

**Quality:**
- WCAG AA contrast verified
- TalkBack / VoiceOver labels on all interactive elements
- Dynamic Type / Font Scaling supported
- Spanish copy human-approved throughout
- Theological content human-approved throughout
- Privacy Policy and Terms of Service published

**Store:**
- Live on both stores
- Listings localized for minimum 5 territories
- Data Safety form (Android) and Privacy Manifest (iOS) complete
- Both subscription products purchasable in tested territories

---

## 9. OPERATIONAL PROTOCOLS

### 9.1 Daily Rhythm (Non-Dev Architect)

**Morning — 60 minutes**
- Review overnight agent diffs
- Accept, revert, or redirect
- Write today's acceptance criteria as specific checklist items
- Update project log

**Midday — 4 hours**
- One Antigravity autonomous session against today's checklist
- You run parallel work: real-device testing, content review, design decisions, App Store / Play Store admin

**Evening — 2 hours**
- Manual integration and verification of agent work
- Architectural decisions and red team for tomorrow
- Update this framework if any locked decision needs revision (rare)

### 9.2 Weekly Review (Friday, 90 minutes)

- Codebase coherence pass — Kimi reads whole repo, flags convention drift
- Burn-down against current phase gate
- Update Risk Registry status
- Confirm next week's phase position

### 9.3 Phase Gate Review

- Before declaring a phase gate passed, run a Claude (web) red team session
- Provide: phase number, all gate criteria, evidence of each criterion met
- Claude verifies, identifies any gap, approves or sends back

### 9.4 Emergency Escalation

If something breaks production assumptions (Apple denial, Google Play policy change, critical hallucination introduced into main):
1. Halt all agent sessions
2. Open Claude (web) for a strategic re-evaluation session
3. Update this framework with the new reality
4. Resume only after framework v-bump approved by human

---

## APPENDIX A — CORRECTED INTERCEPTCONTROLLER REFERENCE

This is the canonical template. Agents reference this when generating any controller or ViewModel.

```kotlin
package com.panoplia.caminosanctus.intercept

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.panoplia.caminosanctus.platform.ioDispatcher

enum class Phase { ENCUENTRO, DISCIPLINA, PROFUNDIDAD }
enum class InteractionMode { BREATHING, REFLECTION_TAP, TYPING }

sealed class UmbralState {
    object Idle : UmbralState()
    data class SoftIntercept(
        val verse: String,
        val requiredWaitMs: Long
    ) : UmbralState()
    data class EngagedIntercept(
        val mode: InteractionMode,
        val requiredText: String? = null
    ) : UmbralState()
    data class FullLock(
        val unlockTimeEpoch: Long,
        val emergencyBypassAvailable: Boolean
    ) : UmbralState()
    data class Error(
        val message: String,
        val recoverable: Boolean
    ) : UmbralState()
}

sealed class InterceptIntent {
    data class AppLaunched(val packageName: String) : InterceptIntent()
    object WaitCompleted : InterceptIntent()
    data class TextSubmitted(val text: String) : InterceptIntent()
    object ReflectionCompleted : InterceptIntent()
    object BreathingCompleted : InterceptIntent()
    object EmergencyBypassRequested : InterceptIntent()
}

class InterceptController(
    private val contentRepository: ContentRepository,
    private val interceptLogRepository: InterceptLogRepository,
    private val phaseEngine: PhaseEngine,
    private val vpnManager: VpnManager,
    private val userPreferences: UserPreferences,
    private val scope: CoroutineScope
) {
    private val _uiState = MutableStateFlow<UmbralState>(UmbralState.Idle)
    val uiState: StateFlow<UmbralState> = _uiState.asStateFlow()

    fun processIntent(intent: InterceptIntent) {
        scope.launch {
            try {
                when (intent) {
                    is InterceptIntent.AppLaunched -> evaluateLaunch(intent.packageName)
                    is InterceptIntent.WaitCompleted -> completeUnlock(completed = true)
                    is InterceptIntent.TextSubmitted -> evaluateSubmission(intent.text)
                    is InterceptIntent.ReflectionCompleted -> completeUnlock(completed = true)
                    is InterceptIntent.BreathingCompleted -> completeUnlock(completed = true)
                    is InterceptIntent.EmergencyBypassRequested -> handleEmergencyBypass()
                }
            } catch (e: Exception) {
                _uiState.value = UmbralState.Error(
                    message = e.message ?: "intercept_error_unknown",
                    recoverable = true
                )
            }
        }
    }

    private suspend fun evaluateLaunch(packageName: String) {
        val phase = withContext(ioDispatcher) {
            phaseEngine.determinePhase(packageName)
        }

        when (phase) {
            Phase.ENCUENTRO -> {
                val card = withContext(ioDispatcher) {
                    contentRepository.getRankedCard(packageName)
                }
                vpnManager.engageSinkhole(packageName)
                _uiState.value = UmbralState.SoftIntercept(card.body, 15_000L)
            }
            Phase.DISCIPLINA -> {
                val mode = userPreferences.preferredInteractionMode()
                val requiredText = if (mode == InteractionMode.TYPING) {
                    userPreferences.customPhrase()
                } else null
                vpnManager.engageSinkhole(packageName)
                _uiState.value = UmbralState.EngagedIntercept(mode, requiredText)
            }
            Phase.PROFUNDIDAD -> {
                val lockDurationMs = userPreferences.lockDurationMinutes() * 60_000L
                val unlockTime = System.currentTimeMillis() + lockDurationMs
                vpnManager.engageSinkhole(packageName)
                _uiState.value = UmbralState.FullLock(
                    unlockTimeEpoch = unlockTime,
                    emergencyBypassAvailable = true
                )
            }
        }
    }

    private suspend fun completeUnlock(completed: Boolean) {
        vpnManager.releaseSinkhole()
        withContext(ioDispatcher) {
            interceptLogRepository.logCompletion(_uiState.value, completed)
        }
        _uiState.value = UmbralState.Idle
    }

    private suspend fun evaluateSubmission(text: String) {
        val currentState = _uiState.value
        if (currentState !is UmbralState.EngagedIntercept) return
        val target = currentState.requiredText ?: return

        val normalized = text.normalizeForComparison()
        val targetNormalized = target.normalizeForComparison()

        if (normalized == targetNormalized) {
            completeUnlock(completed = true)
        }
        // Wrong input: state unchanged, UI shows gentle feedback
    }

    private suspend fun handleEmergencyBypass() {
        val currentState = _uiState.value
        if (currentState !is UmbralState.FullLock) return
        if (!currentState.emergencyBypassAvailable) return

        withContext(ioDispatcher) {
            interceptLogRepository.logEmergencyBypass()
        }
        vpnManager.releaseSinkhole()
        _uiState.value = UmbralState.Idle
    }
}

// expect declaration in commonMain — diacritic and case insensitive
expect fun String.normalizeForComparison(): String
```

---

## APPENDIX B — PERMISSION ONBOARDING COPY (SPANISH, REVIEWED)

Each permission screen explains what, why, and offers skip.

**Usage Access (Android):**
> "Camino Sanctus necesita ver qué aplicaciones estás abriendo para poder ofrecerte una pausa sagrada en el momento justo. No leemos contenido, solo nombres de aplicaciones."
> [Conceder acceso] [Por ahora no]

**VPN (Android):**
> "Para detener el flujo de redes sociales en el momento exacto de la pausa, Camino Sanctus crea una conexión local en tu dispositivo. No enviamos datos a servidores externos. Todo permanece en tu teléfono."
> [Activar conexión local] [Por ahora no]

**Display over apps (Android):**
> "Camino Sanctus dibuja la pantalla de oración sobre la aplicación que intentas abrir. Sin este permiso, la pausa llegaría demasiado tarde."
> [Permitir] [Por ahora no]

**Screen Time (iOS):**
> "Camino Sanctus utiliza el control de tiempo de pantalla de Apple para ofrecerte momentos de reflexión antes de entrar a redes sociales. Apple protege tu privacidad — nosotros nunca vemos los nombres de las aplicaciones que eliges."
> [Continuar] [Por ahora no]

---

## APPENDIX C — APPLE FAMILYCONTROLS ENTITLEMENT FRAMING

When filing the entitlement request:

**Category:** Digital Wellbeing / Screen Time Management
**Use case description:**
> "Camino Sanctus is a Spanish-language digital wellbeing app that helps users build intentional habits with social media and entertainment apps through brief contemplative pauses. Users select which apps they want to add friction to. Before opening a selected app, a 15-second reflection card is presented. After repeated bypasses, the friction escalates to a 30-second guided breathing exercise or a brief contemplative prompt. Users retain full control — they can disable monitoring at any time, and an emergency bypass is always available. The app does not collect, transmit, or store any information about which apps users open beyond local on-device aggregate counts used for the friction logic."

**Never say:** "VPN," "blocker," "lock," "restriction," "parental control," "punish."
**Always say:** "Mindfulness," "intentional pauses," "user-chosen friction," "wellbeing," "reflection."

Include in the request:
- Link to App Store Connect record (even if app not yet submitted)
- Screenshot of the proposed shield UI (showing the gentle, contemplative tone)
- Confirmation that users explicitly select monitored apps (not automatic)
- Confirmation that an always-available bypass exists

---

**END OF MASTER FRAMEWORK v1.0**

Next action: File Apple FamilyControls entitlement. Begin Phase 0.
