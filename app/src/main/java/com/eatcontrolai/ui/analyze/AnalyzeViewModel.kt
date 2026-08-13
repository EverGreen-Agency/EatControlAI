package com.eatcontrolai.ui.analyze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.eatcontrolai.AppContainer
import com.eatcontrolai.core.model.Allergen
import com.eatcontrolai.core.model.UserProfile
import com.eatcontrolai.glasses.MockScene
import com.eatcontrolai.glasses.MockScenes
import com.eatcontrolai.orchestration.InteractionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AnalyzeUiState(
    val scenes: List<MockScene>,
    val selectedSceneId: String,
    val profile: UserProfile,
    val glassesConnected: Boolean = false,
    val isAnalyzing: Boolean = false,
    val result: InteractionResult? = null,
    val error: String? = null
) {
    val selectedScene: MockScene
        get() = scenes.first { it.id == selectedSceneId }
}

class AnalyzeViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AnalyzeUiState(
            scenes = MockScenes.all,
            selectedSceneId = MockScenes.default.id,
            profile = container.demoProfile
        )
    )
    val uiState: StateFlow<AnalyzeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            container.glasses.connect()
            _uiState.update { it.copy(glassesConnected = container.glasses.isConnected) }
        }
    }

    fun selectScene(sceneId: String) {
        container.glasses.selectScene(sceneId)
        _uiState.update { it.copy(selectedSceneId = sceneId, result = null, error = null) }
    }

    /** Permite alternar o perfil ao vivo — o mesmo rótulo muda de decisão conforme a restrição. */
    fun toggleRestriction(allergen: Allergen) {
        _uiState.update { state ->
            val next = state.profile.restrictions.toMutableSet()
            if (!next.remove(allergen)) next += allergen
            state.copy(profile = state.profile.copy(restrictions = next), result = null)
        }
    }

    fun analyze() {
        if (_uiState.value.isAnalyzing) return
        _uiState.update { it.copy(isAnalyzing = true, error = null) }

        viewModelScope.launch {
            runCatching { container.orchestrator.analyzeLabel(_uiState.value.profile) }
                .onSuccess { result ->
                    _uiState.update { it.copy(isAnalyzing = false, result = result) }
                }
                .onFailure { throwable ->
                    // NFR-004: falha degrada para mensagem explícita, nunca para uma afirmação de segurança.
                    _uiState.update {
                        it.copy(
                            isAnalyzing = false,
                            error = throwable.message ?: "Falha ao analisar o rótulo."
                        )
                    }
                }
        }
    }

    class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AnalyzeViewModel(container) as T
    }
}
