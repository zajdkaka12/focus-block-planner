package com.focusblock.planner.data.repository

import com.focusblock.planner.core.config.GameConfig
import com.focusblock.planner.data.local.PreferencesStorage
import com.focusblock.planner.domain.model.AppSettings
import com.focusblock.planner.domain.repository.SettingsRepository

private const val KEY_DAY_START = "day_start"
private const val KEY_DAY_END = "day_end"
private const val KEY_DEFAULT_LENGTH = "default_length"
private const val KEY_AUTO_START = "auto_start"
private const val KEY_HAPTICS = "haptics"

class SettingsRepositoryImpl(private val storage: PreferencesStorage) : SettingsRepository {

    override fun load(): AppSettings = AppSettings(
        dayStartMinutes = storage.readInt(KEY_DAY_START, GameConfig.DEFAULT_DAY_START_MINUTES),
        dayEndMinutes = storage.readInt(KEY_DAY_END, GameConfig.DEFAULT_DAY_END_MINUTES),
        defaultBlockMinutes = storage.readInt(
            KEY_DEFAULT_LENGTH,
            GameConfig.DEFAULT_BLOCK_MINUTES
        ),
        autoStartNext = storage.readBoolean(KEY_AUTO_START, false),
        hapticsEnabled = storage.readBoolean(KEY_HAPTICS, true)
    )

    override fun save(settings: AppSettings) {
        storage.writeInt(KEY_DAY_START, settings.dayStartMinutes)
        storage.writeInt(KEY_DAY_END, settings.dayEndMinutes)
        storage.writeInt(KEY_DEFAULT_LENGTH, settings.defaultBlockMinutes)
        storage.writeBoolean(KEY_AUTO_START, settings.autoStartNext)
        storage.writeBoolean(KEY_HAPTICS, settings.hapticsEnabled)
    }
}
