package com.focusblock.planner.domain.usecase

import com.focusblock.planner.domain.model.FocusBlock

class CompleteBlockUseCase {

    operator fun invoke(
        blocks: List<FocusBlock>,
        blockId: Long,
        elapsedMinutes: Int,
        completedEarly: Boolean
    ): List<FocusBlock> = blocks.map { block ->
        if (block.id != blockId) {
            block
        } else {
            val credited = if (completedEarly) {
                elapsedMinutes.coerceIn(0, block.lengthMinutes)
            } else {
                block.lengthMinutes
            }
            block.copy(done = true, focusedMinutes = credited)
        }
    }
}
