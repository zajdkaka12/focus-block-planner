package com.focusblock.planner.domain.model

data class DayPlan(val blocks: List<FocusBlock> = emptyList()) {

    val plannedMinutes: Int
        get() = blocks.sumOf { it.lengthMinutes }

    val focusedMinutes: Int
        get() = blocks.filter { it.done }.sumOf { it.focusedMinutes }

    val doneCount: Int
        get() = blocks.count { it.done }

    val isEmpty: Boolean
        get() = blocks.isEmpty()

    fun nextPending(afterMinutes: Int): FocusBlock? =
        blocks.filterNot { it.done }
            .sortedBy { it.startMinutes }
            .firstOrNull { it.startMinutes >= afterMinutes } ?: blocks.firstOrNull { !it.done }
}
