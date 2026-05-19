package com.panoplia.caminosanctus.data

import com.panoplia.caminosanctus.db.CaminoDatabase
import com.panoplia.caminosanctus.domain.InteractionMode
import com.panoplia.caminosanctus.domain.SubscriptionStatus
import com.panoplia.caminosanctus.domain.UserProfile
import com.panoplia.caminosanctus.domain.UserProfileRepository
import com.panoplia.caminosanctus.platform.ioDispatcher
import kotlinx.coroutines.withContext

class UserProfileRepositoryImpl(private val db: CaminoDatabase) : UserProfileRepository {

    override suspend fun getProfile(): UserProfile? =
        withContext(ioDispatcher) {
            db.userProfileQueries.getProfile().executeAsOneOrNull()?.toModel()
        }

    override suspend fun initProfile(installTimestamp: Long, lastActiveDate: Long) =
        withContext(ioDispatcher) {
            db.userProfileQueries.initProfile(installTimestamp, lastActiveDate)
        }

    override suspend fun updateStreak(streak: Long) =
        withContext(ioDispatcher) {
            db.userProfileQueries.updateStreak(streak)
        }

    override suspend fun updateSubscription(status: SubscriptionStatus, expiresAt: Long?) =
        withContext(ioDispatcher) {
            db.userProfileQueries.updateSubscription(status.name, expiresAt)
        }

    override suspend fun updateInteractionMode(mode: InteractionMode, customPhrase: String?) =
        withContext(ioDispatcher) {
            db.userProfileQueries.updateInteractionMode(mode.name, customPhrase)
        }

    override suspend fun incrementPaywallShown(timestamp: Long) =
        withContext(ioDispatcher) {
            db.userProfileQueries.incrementPaywallShown(timestamp)
        }

    override suspend fun markReviewPromptShown() =
        withContext(ioDispatcher) {
            db.userProfileQueries.markReviewPromptShown()
        }

    override suspend fun completeOnboarding() =
        withContext(ioDispatcher) {
            db.userProfileQueries.completeOnboarding()
        }

    private fun com.panoplia.caminosanctus.db.User_profile.toModel() = UserProfile(
        streak                = streak,
        longestStreak         = longest_streak,
        totalIntercepts       = total_intercepts,
        preferredInteractionMode = runCatching {
            InteractionMode.valueOf(preferred_interaction_mode)
        }.getOrDefault(InteractionMode.BREATHING),
        customPhrase          = custom_phrase,
        lockDurationMinutes   = lock_duration_minutes,
        subscriptionStatus    = runCatching {
            SubscriptionStatus.valueOf(subscription_status)
        }.getOrDefault(SubscriptionStatus.FREE),
        subscriptionExpiresAt = subscription_expires_at,
        installTimestamp      = install_timestamp,
        lastActiveDate        = last_active_date,
        paywallShownCount     = paywall_shown_count,
        reviewPromptShown     = review_prompt_shown == 1L,
        onboardingComplete    = onboarding_complete == 1L
    )
}
