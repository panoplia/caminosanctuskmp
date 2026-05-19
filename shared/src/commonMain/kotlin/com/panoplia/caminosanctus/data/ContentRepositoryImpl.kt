package com.panoplia.caminosanctus.data

import com.panoplia.caminosanctus.db.CaminoDatabase
import com.panoplia.caminosanctus.domain.ContentCard
import com.panoplia.caminosanctus.domain.ContentRepository
import com.panoplia.caminosanctus.platform.ioDispatcher
import kotlinx.coroutines.withContext

class ContentRepositoryImpl(private val db: CaminoDatabase) : ContentRepository {

    override suspend fun getRankedCard(depthTier: Int): ContentCard =
        withContext(ioDispatcher) {
            val row = db.contentCardsQueries
                .getRankedCard(depthTier.toLong())
                .executeAsOneOrNull()
                ?: db.contentCardsQueries
                    .getAnyRankedCard()
                    .executeAsOneOrNull()
                ?: error("No content cards in database. Seed before use.")
            row.toModel()
        }

    override suspend fun updateDisplayStats(cardId: String, timestamp: Long) =
        withContext(ioDispatcher) {
            db.contentCardsQueries.updateDisplayStats(timestamp, cardId)
        }

    override suspend fun insertCard(card: ContentCard) =
        withContext(ioDispatcher) {
            db.contentCardsQueries.insertCard(
                id                   = card.id,
                category             = card.category,
                title                = card.title,
                body                 = card.body,
                source               = card.source,
                theologicalDepthTier = card.theologicalDepthTier.toLong(),
                liturgicalTheme      = card.liturgicalTheme,
                emotionalState       = card.emotionalState,
                displayCount         = card.displayCount,
                lastShownAt          = card.lastShownAt,
                userRating           = card.userRating?.toLong(),
                createdAt            = card.createdAt
            )
        }

    override suspend fun countCards(): Long =
        withContext(ioDispatcher) {
            db.contentCardsQueries.countCards().executeAsOne()
        }

    private fun com.panoplia.caminosanctus.db.Content_cards.toModel() = ContentCard(
        id                   = id,
        category             = category,
        title                = title,
        body                 = body,
        source               = source,
        theologicalDepthTier = theological_depth_tier.toInt(),
        liturgicalTheme      = liturgical_theme,
        emotionalState       = emotional_state,
        displayCount         = display_count,
        lastShownAt          = last_shown_at,
        userRating           = user_rating?.toInt(),
        createdAt            = created_at
    )
}
