package com.focusblock.planner.domain.repository

import com.focusblock.planner.domain.model.DayRecord

interface StatsRepository {
    fun week(): List<DayRecord>
    fun recordToday(blocksDone: Int, minutesByCategory: Map<String, Int>)
    fun streak(): Int
    fun bestStreak(): Int
    fun saveStreak(streak: Int, best: Int)
    fun todayIndex(): Int
    fun nowMinutes(): Int
}
