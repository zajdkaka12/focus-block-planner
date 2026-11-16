package com.focusblock.planner.domain.usecase

import com.focusblock.planner.domain.model.DayRecord
import com.focusblock.planner.domain.model.WeekStats

class BuildWeekStatsUseCase {

    operator fun invoke(records: List<DayRecord>): WeekStats {
        val byCategory = mutableMapOf<String, Int>()
        records.forEach { record ->
            record.minutesByCategory.forEach { (categoryId, minutes) ->
                byCategory[categoryId] = (byCategory[categoryId] ?: 0) + minutes
            }
        }
        val total = records.sumOf { it.totalMinutes }
        val bestIndex = records.withIndex()
            .filter { it.value.totalMinutes > 0 }
            .maxByOrNull { it.value.totalMinutes }
            ?.index ?: -1
        return WeekStats(
            days = records,
            minutesByCategory = byCategory,
            totalMinutes = total,
            blocksDone = records.sumOf { it.blocksDone },
            bestDayIndex = bestIndex
        )
    }
}
