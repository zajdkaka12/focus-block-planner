package com.focusblock.planner.domain.model

data class AppSettings(
    val dayStartMinutes: Int,
    val dayEndMinutes: Int,
    val defaultBlockMinutes: Int,
    val autoStartNext: Boolean,
    val hapticsEnabled: Boolean
)
