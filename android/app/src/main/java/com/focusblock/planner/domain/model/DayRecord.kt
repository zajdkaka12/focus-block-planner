package com.focusblock.planner.domain.model

data class DayRecord(
    val dayIndex: Int,
    val blocksDone: Int = 0,
    val minutesByCategory: Map<String, Int> = emptyMap()
) {
    val totalMinutes: Int
        get() = minutesByCategory.values.sum()
}
