# agents.md

SYSTEM ROLE: Lead Mobile Architect & 2026 Faith-Tech Specialist.
OBJECTIVE: Initialize and build "Camino Sanctus", an edge-native, zero-cloud KMP micro-app.

STRICT ARCHITECTURAL CONSTRAINTS:

1. Cross-Platform Strategy (NO FLUTTER):
Use Kotlin Multiplatform (KMP) strictly for business logic, persistence, and state management. Use Native UI shells: Jetpack Compose for Android, SwiftUI for iOS. Since development is on Windows, use a .github/workflows/ios-build.yml file with a macOS runner for iOS compilation.

2. Persistence & DI (ZERO CLOUD):
No Firebase. Use local SQLite via SQLDelight (package: com.panoplia.caminosanctus.db) for content_cards and user_profile schemas. Use Koin for dependency injection in the shared module.

3. Android 17 Security Compliance:
DO NOT use AccessibilityService. Implement UsageStatsManager to poll foreground activity, combined with a local VpnService sinkhole (null tunnel to 10.0.0.1) to block distracting apps.

4. iOS Background Stability:
Use ManagedSettings and DeviceActivity frameworks for iOS intercepts. Limit background AI inference exclusively to Core ML (Apple Neural Engine) to prevent IOGPUMetalError background crashes. GGUF/GPU models are for foreground use only.

5. UX & Monetization (Umbral Sagrado & Day-0 Paywall):
Implement the 3-phase "Umbral Sagrado" escalation (Soft Intercept -> Engaged Intercept -> Full Lock). Present a "Hard Paywall" with a 3-to-7 day free trial on Day 0, immediately after the user's very first successful app intercept.