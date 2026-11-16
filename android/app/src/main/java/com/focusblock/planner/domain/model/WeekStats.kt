package com.focusblock.planner.domain.model

data class WeekStats(
    val days: List<DayRecord> = emptyList(),
    val minutesByCategory: Map<String, Int> = emptyMap(),
    val totalMinutes: Int = 0,
    val blocksDone: Int = 0,
    val bestDayIndex: Int = -1
) {
    val isEmpty: Boolean
        get() = totalMinutes == 0
}
