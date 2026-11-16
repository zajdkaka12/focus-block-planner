package com.focusblock.planner.presentation.blockeditor

import com.focusblock.planner.core.config.GameConfig
import com.focusblock.planner.domain.model.Category

data class BlockEditorUiState(
    val loading: Boolean = true,
    val blockId: Long = -1L,
    val title: String = "",
    val categories: List<Category> = emptyList(),
    val categoryId: String = "",
    val lengthMinutes: Int = GameConfig.DEFAULT_BLOCK_MINUTES,
    val startMinutes: Int = GameConfig.DEFAULT_DAY_START_MINUTES,
    val dayStartMinutes: Int = GameConfig.DEFAULT_DAY_START_MINUTES,
    val dayEndMinutes: Int = GameConfig.DEFAULT_DAY_END_MINUTES,
    val editing: Boolean = false,
    val hapticsEnabled: Boolean = true,
    val showFitError: Boolean = false,
    val showTitleError: Boolean = false,
    val confirmTick: Int = 0
) {
    val canShrink: Boolean
        get() = lengthMinutes > GameConfig.MIN_BLOCK_MINUTES

    val canGrow: Boolean
        get() = lengthMinutes < GameConfig.MAX_BLOCK_MINUTES

    val canMoveEarlier: Boolean
        get() = startMinutes - GameConfig.SLOT_MINUTES >= dayStartMinutes

    val canMoveLater: Boolean
        get() = startMinutes + GameConfig.SLOT_MINUTES + lengthMinutes <= dayEndMinutes
}
