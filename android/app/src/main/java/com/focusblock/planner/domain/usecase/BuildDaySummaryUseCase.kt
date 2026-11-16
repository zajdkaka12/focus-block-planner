package com.focusblock.planner.domain.usecase

import com.focusblock.planner.core.config.GameConfig
import com.focusblock.planner.domain.model.Category
import com.focusblock.planner.domain.model.CategorySegment
import com.focusblock.planner.domain.model.DayPlan
import com.focusblock.planner.domain.model.DaySummary
import kotlin.math.max

class BuildDaySummaryUseCase {

    operator fun invoke(
        plan: DayPlan,
        categories: List<Category>,
        previousStreak: Int,
        storedBestStreak: Int
    ): DaySummary {
        val focused = plan.focusedMinutes
        val minutesByCategory = minutesByCategory(plan)
        val segments = categories
            .map { category ->
                CategorySegment(
                    categoryId = category.id,
                    name = category.name,
                    colorArgb = category.colorArgb,
                    minutes = minutesByCategory[category.id] ?: 0
                )
            }
            .filter { it.minutes > 0 }
        val streak = if (focused >= GameConfig.STREAK_MIN_MINUTES) previousStreak + 1 else 0
        val best = max(streak, storedBestStreak)
        return DaySummary(
            focusedMinutes = focused,
            blocksDone = plan.doneCount,
            blocksPlanned = plan.blocks.size,
            streak = streak,
            bestStreak = best,
            newBest = streak > storedBestStreak && streak > 0,
            topCategoryName = segments.maxByOrNull { it.minutes }?.name.orEmpty(),
            segments = segments.sortedByDescending { it.minutes }
        )
    }

    fun minutesByCategory(plan: DayPlan): Map<String, Int> =
        plan.blocks.filter { it.done && it.focusedMinutes > 0 }
            .groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.focusedMinutes } }
}
