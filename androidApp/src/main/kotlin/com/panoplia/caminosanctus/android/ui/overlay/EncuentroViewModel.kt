package com.panoplia.caminosanctus.android.ui.overlay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panoplia.caminosanctus.domain.ContentCard
import com.panoplia.caminosanctus.domain.ContentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EncuentroState {
    object Loading : EncuentroState()
    data class Active(val card: ContentCard, val remainingSeconds: Int) : EncuentroState()
    object Unlocked : EncuentroState()
    data class Error(val message: String) : EncuentroState()
}

class EncuentroViewModel(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EncuentroState>(EncuentroState.Loading)
    val uiState: StateFlow<EncuentroState> = _uiState.asStateFlow()

    fun initializeIntercept(depthTier: Int = 1) {
        viewModelScope.launch {
            try {
                val card = contentRepository.getRankedCard(depthTier)
                startFrictionTimer(card, durationSeconds = 15)
            } catch (e: Exception) {
                // No cards in DB yet — fail open, never trap user
                _uiState.value = EncuentroState.Unlocked
            }
        }
    }

    private fun startFrictionTimer(card: ContentCard, durationSeconds: Int) {
        viewModelScope.launch {
            for (i in durationSeconds downTo 1) {
                _uiState.value = EncuentroState.Active(card, i)
                delay(1_000)
            }
            _uiState.value = EncuentroState.Active(card, 0)
        }
    }

    fun grantAccess() {
        _uiState.value = EncuentroState.Unlocked
    }
}
