package com.focusblock.planner.presentation.settings

import com.focusblock.planner.core.config.GameConfig
import com.focusblock.planner.domain.model.AppSettings

data class SettingsUiState(
    val loading: Boolean = true,
    val settings: AppSettings = AppSettings(
        dayStartMinutes = GameConfig.DEFAULT_DAY_START_MINUTES,
        dayEndMinutes = GameConfig.DEFAULT_DAY_END_MINUTES,
        defaultBlockMinutes = GameConfig.DEFAULT_BLOCK_MINUTES,
        autoStartNext = false,
        hapticsEnabled = true
    ),
    val confirmReset: Boolean = false
) {
    val canStartEarlier: Boolean
        get() = settings.dayStartMinutes > GameConfig.MIN_DAY_START_MINUTES

    val canStartLater: Boolean
        get() = settings.dayStartMinutes < GameConfig.MAX_DAY_START_MINUTES

    val canEndEarlier: Boolean
        get() = settings.dayEndMinutes > GameConfig.MIN_DAY_END_MINUTES

    val canEndLater: Boolean
        get() = settings.dayEndMinutes < GameConfig.MAX_DAY_END_MINUTES

    val canShrinkBlock: Boolean
        get() = settings.defaultBlockMinutes > GameConfig.MIN_BLOCK_MINUTES

    val canGrowBlock: Boolean
        get() = settings.defaultBlockMinutes < GameConfig.MAX_BLOCK_MINUTES
}
