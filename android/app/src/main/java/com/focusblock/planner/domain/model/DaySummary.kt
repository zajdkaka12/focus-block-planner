package com.focusblock.planner.domain.model

data class CategorySegment(
    val categoryId: String,
    val name: String,
    val colorArgb: Long,
    val minutes: Int
)

data class DaySummary(
    val focusedMinutes: Int = 0,
    val blocksDone: Int = 0,
    val blocksPlanned: Int = 0,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val newBest: Boolean = false,
    val topCategoryName: String = "",
    val segments: List<CategorySegment> = emptyList()
)
