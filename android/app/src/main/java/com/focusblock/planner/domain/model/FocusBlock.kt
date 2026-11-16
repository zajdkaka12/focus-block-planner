package com.focusblock.planner.domain.model

data class FocusBlock(
    val id: Long,
    val title: String,
    val categoryId: String,
    val startMinutes: Int,
    val lengthMinutes: Int,
    val done: Boolean = false,
    val focusedMinutes: Int = 0
) {
    val endMinutes: Int
        get() = startMinutes + lengthMinutes

    fun overlaps(other: FocusBlock): Boolean =
        startMinutes < other.endMinutes && other.startMinutes < endMinutes
}
