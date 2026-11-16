package com.focusblock.planner.domain.usecase

import com.focusblock.planner.domain.model.FocusBlock

class MoveBlockUseCase(private val placeBlock: PlaceBlockUseCase) {

    operator fun invoke(
        blocks: List<FocusBlock>,
        blockId: Long,
        newStartMinutes: Int,
        dayStartMinutes: Int,
        dayEndMinutes: Int
    ): List<FocusBlock>? {
        val moved = blocks.firstOrNull { it.id == blockId } ?: return null
        if (moved.startMinutes == newStartMinutes) return blocks
        return placeBlock(
            blocks,
            moved.copy(startMinutes = newStartMinutes),
            dayStartMinutes,
            dayEndMinutes
        )
    }
}
