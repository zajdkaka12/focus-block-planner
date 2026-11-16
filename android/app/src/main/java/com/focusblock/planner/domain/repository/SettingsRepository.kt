package com.focusblock.planner.domain.repository

import com.focusblock.planner.domain.model.AppSettings

interface SettingsRepository {
    fun load(): AppSettings
    fun save(settings: AppSettings)
}
