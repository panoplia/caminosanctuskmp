package com.panoplia.caminosanctus.domain

interface ContentRepository {
    suspend fun getRankedCard(depthTier: Int): ContentCard
    suspend fun updateDisplayStats(cardId: String, timestamp: Long)
    suspend fun insertCard(card: ContentCard)
    suspend fun countCards(): Long
}

interface InterceptLogRepository {
    suspend fun insertLog(
        id: String, timestamp: Long, targetApp: String, durationMs: Long?,
        completed: Boolean, skipped: Boolean, bypassReason: String?,
        contentCardId: String?, phase: Phase
    )
    suspend fun getBypassCountInWindow(targetApp: String, windowStartMs: Long): Long
    suspend fun logEmergencyBypass(id: String, timestamp: Long, targetApp: String)
    suspend fun getTotalCompleted(): Long
}

interface UserProfileRepository {
    suspend fun getProfile(): UserProfile?
    suspend fun initProfile(installTimestamp: Long, lastActiveDate: Long)
    suspend fun updateStreak(streak: Long)
    suspend fun updateSubscription(status: SubscriptionStatus, expiresAt: Long?)
    suspend fun updateInteractionMode(mode: InteractionMode, customPhrase: String?)
    suspend fun incrementPaywallShown(timestamp: Long)
    suspend fun markReviewPromptShown()
    suspend fun completeOnboarding()
}

interface UserPreferences {
    suspend fun preferredInteractionMode(): InteractionMode
    suspend fun customPhrase(): String?
    suspend fun lockDurationMinutes(): Long
    suspend fun subscriptionStatus(): SubscriptionStatus
}
