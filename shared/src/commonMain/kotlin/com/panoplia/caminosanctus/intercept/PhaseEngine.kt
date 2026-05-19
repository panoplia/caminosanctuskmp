package com.panoplia.caminosanctus.intercept

import com.panoplia.caminosanctus.domain.InterceptLogRepository
import com.panoplia.caminosanctus.domain.Phase
import com.panoplia.caminosanctus.platform.currentTimeMs

private const val WINDOW_MS             = 60 * 60 * 1000L
private const val DISCIPLINA_THRESHOLD  = 3L
private const val PROFUNDIDAD_THRESHOLD = 5L

class PhaseEngine(private val interceptLogRepository: InterceptLogRepository) {
    suspend fun determinePhase(packageName: String): Phase {
        val windowStart = currentTimeMs() - WINDOW_MS
        val bypasses = interceptLogRepository.getBypassCountInWindow(packageName, windowStart)
        return when {
            bypasses >= PROFUNDIDAD_THRESHOLD -> Phase.PROFUNDIDAD
            bypasses >= DISCIPLINA_THRESHOLD  -> Phase.DISCIPLINA
            else                              -> Phase.ENCUENTRO
        }
    }
}
