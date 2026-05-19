package com.panoplia.caminosanctus.data

import com.panoplia.caminosanctus.db.CaminoDatabase
import com.panoplia.caminosanctus.domain.InterceptLogRepository
import com.panoplia.caminosanctus.domain.Phase
import com.panoplia.caminosanctus.platform.currentTimeMs
import com.panoplia.caminosanctus.platform.ioDispatcher
import kotlinx.coroutines.withContext

class InterceptLogRepositoryImpl(private val db: CaminoDatabase) : InterceptLogRepository {

    override suspend fun insertLog(
        id: String, timestamp: Long, targetApp: String, durationMs: Long?,
        completed: Boolean, skipped: Boolean, bypassReason: String?,
        contentCardId: String?, phase: Phase
    ) = withContext(ioDispatcher) {
        db.interceptLogQueries.insertLog(
            id             = id,
            timestamp      = timestamp,
            targetApp      = targetApp,
            durationMs     = durationMs,
            completed      = if (completed) 1L else 0L,
            skipped        = if (skipped) 1L else 0L,
            bypassReason   = bypassReason,
            contentCardId  = contentCardId,
            phase          = phase.name,
            createdAt      = currentTimeMs()
        )
    }

    override suspend fun getBypassCountInWindow(
        targetApp: String,
        windowStartMs: Long
    ): Long = withContext(ioDispatcher) {
        db.interceptLogQueries
            .getBypassCountInWindow(targetApp, windowStartMs)
            .executeAsOne()
    }

    override suspend fun logEmergencyBypass(
        id: String,
        timestamp: Long,
        targetApp: String
    ) = withContext(ioDispatcher) {
        db.interceptLogQueries.logEmergencyBypass(
            id        = id,
            timestamp = timestamp,
            targetApp = targetApp,
            createdAt = currentTimeMs()
        )
    }

    override suspend fun getTotalCompleted(): Long =
        withContext(ioDispatcher) {
            db.interceptLogQueries.getTotalCompleted().executeAsOne()
        }
}
