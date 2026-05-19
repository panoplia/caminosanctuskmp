package com.panoplia.caminosanctus.intercept

import com.panoplia.caminosanctus.domain.InteractionMode

sealed class UmbralState {
    object Idle : UmbralState()
    data class SoftIntercept(val verse: String, val source: String?, val requiredWaitMs: Long) : UmbralState()
    data class EngagedIntercept(val mode: InteractionMode, val verse: String?, val requiredText: String?) : UmbralState()
    data class FullLock(val unlockTimeEpoch: Long, val emergencyBypassAvailable: Boolean) : UmbralState()
    data class Error(val message: String, val recoverable: Boolean) : UmbralState()
}

sealed class InterceptIntent {
    data class AppLaunched(val packageName: String) : InterceptIntent()
    object WaitCompleted : InterceptIntent()
    data class TextSubmitted(val text: String) : InterceptIntent()
    object ReflectionCompleted : InterceptIntent()
    object BreathingCompleted : InterceptIntent()
    object EmergencyBypassRequested : InterceptIntent()
    object CompanionSessionCompleted : InterceptIntent()
}
