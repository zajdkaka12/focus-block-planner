package com.focusblock.planner.core.navigation

import com.focusblock.planner.domain.model.DaySummary

sealed interface Screen {

    data object Splash : Screen

    data object Menu : Screen

    data object Game : Screen

    data class BlockEditor(val blockId: Long, val startMinutes: Int) : Screen

    data object Categories : Screen

    data object WeeklyReview : Screen

    data object Settings : Screen

    data class GameOver(val summary: DaySummary) : Screen
}
