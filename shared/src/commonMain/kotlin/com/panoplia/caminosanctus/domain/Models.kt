package com.panoplia.caminosanctus.domain

enum class Phase { IDLE, ENCUENTRO, DISCIPLINA, PROFUNDIDAD }
enum class InteractionMode { BREATHING, REFLECTION_TAP, TYPING }
enum class SubscriptionStatus { FREE, MONTHLY, ANNUAL, LIFETIME }

data class ContentCard(
    val id: String,
    val category: String,
    val title: String,
    val body: String,
    val source: String?,
    val theologicalDepthTier: Int,
    val liturgicalTheme: String?,
    val emotionalState: String?,
    val displayCount: Long,
    val lastShownAt: Long?,
    val userRating: Int?,
    val createdAt: Long
)

data class UserProfile(
    val streak: Long,
    val longestStreak: Long,
    val totalIntercepts: Long,
    val preferredInteractionMode: InteractionMode,
    val customPhrase: String?,
    val lockDurationMinutes: Long,
    val subscriptionStatus: SubscriptionStatus,
    val subscriptionExpiresAt: Long?,
    val installTimestamp: Long,
    val lastActiveDate: Long,
    val paywallShownCount: Long,
    val reviewPromptShown: Boolean,
    val onboardingComplete: Boolean
)
