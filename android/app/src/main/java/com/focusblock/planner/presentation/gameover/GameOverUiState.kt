package com.focusblock.planner.presentation.gameover

import com.focusblock.planner.domain.model.DaySummary

data class GameOverUiState(
    val loading: Boolean = true,
    val summary: DaySummary = DaySummary()
)
