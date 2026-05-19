# **Strategic Due Diligence & Architecture Report: Camino Sanctus**

**STATUS:** Active Pivot (Edge-Native Kotlin Multiplatform)

**CORE OBJECTIVE:** Deploy a high-margin, zero-cloud behavioral intercept micro-app targeting the Latin American and US Hispanic demographics.

## ---

**1\. Executive Summary**

The transition of Camino Sanctus from a venture-scale, server-heavy application to an edge-native micro-app represents a structural shift designed to maximize operating margins and retention. The product functions as a behavioral interrupt layer situated between the user's limbic dopamine loop and high-friction applications. The monetizable unit is not the content itself, but rather the micro-moment of reclaimed intentionality. By leveraging localized Small Language Models (SLMs) and a hybrid escalation user experience, the system bypasses the churn fatigue associated with purely punitive application blockers.

## **2\. Market Sociology & Target Demographics**

The Latin American digital landscape requires a nuanced psychological approach distinct from the US evangelical market.

* The regional demographic is experiencing a severe disenchantment with traditional political and religious institutions.

* Despite declining institutional affiliation, belief in the supernatural and spiritual meaning remains exceptionally high.

* The psychological framing of the US evangelical market is highly transactional, whereas the Latin American Catholic culture is fundamentally relational and devotional.

* A purely punitive blocking mechanic feels incongruent to the target audience and is projected to yield 15-20% higher churn rates compared to US benchmarks.

## **3\. Technical Architecture: The Zero-Cloud Engine**

To achieve near-zero operational costs and scale to tens of thousands of users, the application entirely eschews cloud compute dependencies for core interactions.

### **The Kotlin Multiplatform (KMP) Pivot**

* Flutter development is deprecated for this architecture due to platform channel latency and its inability to run inside isolated iOS App Extension processes.

* Kotlin Multiplatform (KMP) is utilized strictly for the shared business logic layer, encompassing content selection algorithms, streak calculations, and SQLDelight database schemas.

* User interface implementations remain fully native, utilizing Jetpack Compose for Android and SwiftUI for iOS.

### **Agent-First Code Generation**

* The codebase utilizes Google Stitch for visual generation, processing textual prompts into production-ready responsive interfaces.

* Google Antigravity serves as the autonomous execution layer, utilizing a strict .agents/skills directory framework to enforce zero-cloud constraints and local storage dependencies.

### **On-Device Inference (Local SLMs)**

* The application utilizes a quantized Small Language Model (SLM) integrated via the llama.cpp framework to execute on consumer mobile CPUs and NPUs.

* Models such as TinyLlama-1.1B or Qwen 3 0.6B converted to 4-bit quantization (q4\_0 GGUF) reduce memory footprints significantly while achieving high token generation speeds.

* Offline functionality is guaranteed, removing network latency as a primary friction point for early-stage user churn.

## **4\. Behavioral UX: Staged Escalation ("Umbral Sagrado")**

To combat the predictable retention cliff drop observed in days 7-14 of pure blocking applications, the user experience utilizes a three-phase hybrid escalation model.

* **Phase 1 (Encuentro / Days 1-7):** A non-punitive, 15-second soft intercept displaying theological insight cards before allowing access to the target app.

* **Phase 2 (Disciplina / Days 8-21):** An engaged intercept requiring micro-interactions, such as a one-question theological reflection or a 30-second guided breath prayer.

* **Phase 3 (Profundidad / Days 22+):** Users self-select into a strict application lock mode or opt for a proactive daily push notification triggering a bounded, 5-turn conversation with the AI companion.

### **Operating System Integration Constraints**

* **iOS Integration:** Relies on the Screen Time API suite, requiring the privileged FamilyControls entitlement and utilizing ShieldConfigurationExtension within a constrained 15MB sandbox limit.

* **Android Integration:** Due to Advanced Protection Mode (AAPM) restrictions on accessibility overlays, the architecture utilizes UsageStatsManager to monitor foreground activity and sinkhole network traffic via local VPN loops.

## **5\. Monetization Strategy: Purchasing Power Parity**

A uniform global pricing strategy destroys unit economics across varied economic populations. The application implements localized territory-specific pricing arrays.

* **Tier 1 (Emerging Economies / Core LatAm):** Pricing is heavily indexed to local realities, operating at $1.99 USD equivalent per month. Argentina mandates a weekly subscription model ($1.99 \- $2.49/week) to mitigate hyper-inflationary risks.

* **Tier 2 (Premium Market / US Diaspora & Spain):** Positioned at $4.99 USD per month or $39.99 annually, capturing high upfront LTV while undercutting competitors like Hallow and Pray.com.

* **The Conversion Trigger:** The paywall is never displayed during the first three sessions. Hard paywalls are dynamically triggered upon the detection of an "Emotional Peak," such as immediately following a verse share, a saved reflection, or the completion of a multi-day streak.

## **6\. The Autonomous Marketing Engine**

The growth funnel is governed by an entirely autonomous n8n workflow designed to produce 5 to 10 short-form videos daily.

* **Content Generation:** n8n pipelines pull raw theological concepts, routing them through LLMs engineered to script aggressive, 30-second viral hooks.

* **Visual Aesthetics ("Dirty Realism"):** The visual language avoids polished commercial AI imagery. The pipeline relies on "found footage" aesthetics generated via Flux LoRAs and Google Veo 3.1, incorporating film grain, motion blur, and low-resolution artifacting.

* **Assembly and Pacing:** Cloud-based APIs assemble the final assets using split-screen canvases and kinetic typography, forcing scene shifts every 1.5 to 2.5 seconds to maximize viewer retention.  
