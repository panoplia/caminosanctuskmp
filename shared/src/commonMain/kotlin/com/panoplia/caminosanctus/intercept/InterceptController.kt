package com.panoplia.caminosanctus.intercept

import com.panoplia.caminosanctus.domain.ContentRepository
import com.panoplia.caminosanctus.domain.InterceptLogRepository
import com.panoplia.caminosanctus.domain.InteractionMode
import com.panoplia.caminosanctus.domain.Phase
import com.panoplia.caminosanctus.domain.UserPreferences
import com.panoplia.caminosanctus.platform.VpnManager
import com.panoplia.caminosanctus.platform.currentTimeMs
import com.panoplia.caminosanctus.platform.generateUuid
import com.panoplia.caminosanctus.platform.ioDispatcher
import com.panoplia.caminosanctus.platform.normalizeForComparison
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private var activePackageName: String? = null
    private var phaseStartMs: Long = 0L

    fun processIntent(intent: InterceptIntent) {
        scope.launch {
            try {
                when (intent) {
                    is InterceptIntent.AppLaunched -> evaluateLaunch(intent.packageName)
                    is InterceptIntent.WaitCompleted -> completeUnlock(completed = true)
                    is InterceptIntent.TextSubmitted -> evaluateSubmission(intent.text)
                    is InterceptIntent.ReflectionCompleted -> completeUnlock(completed = true)
                    is InterceptIntent.BreathingCompleted -> completeUnlock(completed = true)
                    is InterceptIntent.CompanionSessionCompleted -> completeUnlock(completed = true)
                    is InterceptIntent.EmergencyBypassRequested -> handleEmergencyBypass()
                }
            } catch (e: Exception) {
                _uiState.value = UmbralState.Error(e.message ?: "unknown", recoverable = true)
            }
        }
    }

    private suspend fun evaluateLaunch(packageName: String) {
        activePackageName = packageName
        phaseStartMs = currentTimeMs()

        val phase = withContext(ioDispatcher) { phaseEngine.determinePhase(packageName) }

        when (phase) {
            Phase.IDLE -> return
            Phase.ENCUENTRO -> {
                val card = withContext(ioDispatcher) { contentRepository.getRankedCard(depthTier = 1) }
                vpnManager.engageSinkhole(packageName)
                _uiState.value = UmbralState.SoftIntercept(card.body, card.source, 15_000L)
            }
            Phase.DISCIPLINA -> {
                val mode = withContext(ioDispatcher) { userPreferences.preferredInteractionMode() }
                val card = withContext(ioDispatcher) { contentRepository.getRankedCard(depthTier = 2) }
                val requiredText = if (mode == InteractionMode.TYPING)
                    withContext(ioDispatcher) { userPreferences.customPhrase() } else null
                vpnManager.engageSinkhole(packageName)
                _uiState.value = UmbralState.EngagedIntercept(mode, card.body, requiredText)
            }
            Phase.PROFUNDIDAD -> {
                val lockMs = withContext(ioDispatcher) { userPreferences.lockDurationMinutes() * 60_000L }
                vpnManager.engageSinkhole(packageName)
                _uiState.value = UmbralState.FullLock(currentTimeMs() + lockMs, emergencyBypassAvailable = true)
            }
        }
    }

    private suspend fun completeUnlock(completed: Boolean) {
        val pkg = activePackageName ?: return
        val durationMs = currentTimeMs() - phaseStartMs
        vpnManager.releaseSinkhole()
        withContext(ioDispatcher) {
            interceptLogRepository.insertLog(
                id = generateUuid(), timestamp = currentTimeMs(), targetApp = pkg,
                durationMs = durationMs, completed = completed, skipped = !completed,
                bypassReason = null, contentCardId = null, phase = currentPhase()
            )
        }
        activePackageName = null
        _uiState.value = UmbralState.Idle
    }

    private suspend fun evaluateSubmission(text: String) {
        val state = _uiState.value as? UmbralState.EngagedIntercept ?: return
        val target = state.requiredText ?: return
        if (text.normalizeForComparison() == target.normalizeForComparison()) completeUnlock(true)
    }

    private suspend fun handleEmergencyBypass() {
        val state = _uiState.value as? UmbralState.FullLock ?: return
        if (!state.emergencyBypassAvailable) return
        val pkg = activePackageName ?: return
        vpnManager.releaseSinkhole()
        withContext(ioDispatcher) {
            interceptLogRepository.logEmergencyBypass(generateUuid(), currentTimeMs(), pkg)
        }
        activePackageName = null
        _uiState.value = UmbralState.Idle
    }

    private fun currentPhase(): Phase = when (_uiState.value) {
        is UmbralState.SoftIntercept    -> Phase.ENCUENTRO
        is UmbralState.EngagedIntercept -> Phase.DISCIPLINA
        is UmbralState.FullLock         -> Phase.PROFUNDIDAD
        else                            -> Phase.IDLE
    }
}
