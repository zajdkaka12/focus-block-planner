package com.focusblock.planner.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusblock.planner.core.config.GameConfig
import com.focusblock.planner.domain.model.AppSettings
import com.focusblock.planner.domain.repository.PlanRepository
import com.focusblock.planner.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val planRepository: PlanRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val settings = settingsRepository.load()
            _uiState.update { it.copy(loading = false, settings = settings) }
        }
    }

    fun changeDayStart(delta: Int) {
        val current = _uiState.value.settings
        val next = (current.dayStartMinutes + delta).coerceIn(
            GameConfig.MIN_DAY_START_MINUTES,
            GameConfig.MAX_DAY_START_MINUTES
        )
        apply(current.copy(dayStartMinutes = next))
    }

    fun changeDayEnd(delta: Int) {
        val current = _uiState.value.settings
        val next = (current.dayEndMinutes + delta).coerceIn(
            GameConfig.MIN_DAY_END_MINUTES,
            GameConfig.MAX_DAY_END_MINUTES
        )
        apply(current.copy(dayEndMinutes = next))
    }

    fun changeDefaultBlock(delta: Int) {
        val current = _uiState.value.settings
        val next = (current.defaultBlockMinutes + delta).coerceIn(
            GameConfig.MIN_BLOCK_MINUTES,
            GameConfig.MAX_BLOCK_MINUTES
        )
        apply(current.copy(defaultBlockMinutes = next))
    }

    fun setAutoStart(enabled: Boolean) {
        apply(_uiState.value.settings.copy(autoStartNext = enabled))
    }

    fun setHaptics(enabled: Boolean) {
        apply(_uiState.value.settings.copy(hapticsEnabled = enabled))
    }

    fun requestReset() {
        _uiState.update { it.copy(confirmReset = true) }
    }

    fun dismissReset() {
        _uiState.update { it.copy(confirmReset = false) }
    }

    fun confirmReset() {
        _uiState.update { it.copy(confirmReset = false) }
        viewModelScope.launch { planRepository.clear() }
    }

    private fun apply(settings: AppSettings) {
        _uiState.update { it.copy(settings = settings) }
        viewModelScope.launch { settingsRepository.save(settings) }
    }
}
