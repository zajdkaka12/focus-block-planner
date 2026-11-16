package com.focusblock.planner.presentation.menu

import com.focusblock.planner.domain.model.Category

data class MenuUiState(
    val loading: Boolean = true,
    val blockCount: Int = 0,
    val plannedMinutes: Int = 0,
    val focusedMinutes: Int = 0,
    val streak: Int = 0,
    val categories: List<Category> = emptyList(),
    val weekMinutes: List<Int> = emptyList()
) {
    val hasStats: Boolean
        get() = focusedMinutes > 0 && streak > 0

    val weekMax: Int
        get() = weekMinutes.maxOrNull() ?: 0
}
