package com.focusblock.planner.presentation.game

import com.focusblock.planner.core.config.GameConfig
import com.focusblock.planner.domain.model.Category
import com.focusblock.planner.domain.model.DaySummary
import com.focusblock.planner.domain.model.FocusBlock

enum class GameMode { Planning, Running }

sealed interface GameEvent {
    data class OpenEditor(val blockId: Long, val startMinutes: Int) : GameEvent
    data class DayComplete(val summary: DaySummary) : GameEvent
}

data class GameUiState(
    val loading: Boolean = true,
    val blocks: List<FocusBlock> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedBlockId: Long? = null,
    val selectedCategoryId: String = "",
    val mode: GameMode = GameMode.Planning,
    val activeBlockId: Long? = null,
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 1,
    val paused: Boolean = false,
    val dayStartMinutes: Int = GameConfig.DEFAULT_DAY_START_MINUTES,
    val dayEndMinutes: Int = GameConfig.DEFAULT_DAY_END_MINUTES,
    val nowMinutes: Int = 0,
    val errorVisible: Boolean = false
) {
    val plannedMinutes: Int
        get() = blocks.sumOf { it.lengthMinutes }

    val focusedMinutes: Int
        get() = blocks.filter { it.done }.sumOf { it.focusedMinutes }

    val blocksDone: Int
        get() = blocks.count { it.done }

    val activeBlock: FocusBlock?
        get() = blocks.firstOrNull { it.id == activeBlockId }

    val progress: Float
        get() = if (totalSeconds <= 0) 0f else 1f - remainingSeconds.toFloat() / totalSeconds

    val totalSlots: Int
        get() = ((dayEndMinutes - dayStartMinutes) / GameConfig.SLOT_MINUTES).coerceAtLeast(1)

    fun categoryFor(categoryId: String): Category? = categories.firstOrNull { it.id == categoryId }
}
