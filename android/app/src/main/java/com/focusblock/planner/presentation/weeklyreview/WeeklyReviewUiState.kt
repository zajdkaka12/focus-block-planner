package com.focusblock.planner.presentation.weeklyreview

import com.focusblock.planner.domain.model.Category
import com.focusblock.planner.domain.model.WeekStats

data class WeeklyReviewUiState(
    val loading: Boolean = true,
    val stats: WeekStats = WeekStats(),
    val categories: List<Category> = emptyList()
) {
    val maxCategoryMinutes: Int
        get() = stats.minutesByCategory.values.maxOrNull() ?: 0

    val maxDayMinutes: Int
        get() = stats.days.maxOfOrNull { it.totalMinutes } ?: 0

    val isEmpty: Boolean
        get() = !loading && stats.isEmpty
}
