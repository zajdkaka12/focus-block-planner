package com.focusblock.planner.domain.usecase

import com.focusblock.planner.domain.model.FocusBlock

class PlaceBlockUseCase {

    operator fun invoke(
        blocks: List<FocusBlock>,
        candidate: FocusBlock,
        dayStartMinutes: Int,
        dayEndMinutes: Int
    ): List<FocusBlock>? {
        if (candidate.startMinutes < dayStartMinutes) return null
        if (candidate.endMinutes > dayEndMinutes) return null
        val others = blocks.filterNot { it.id == candidate.id }
        if (others.any { it.overlaps(candidate) }) return null
        return (others + candidate).sortedBy { it.startMinutes }
    }
}
