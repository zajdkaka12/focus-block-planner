package com.focusblock.planner.domain.repository

import com.focusblock.planner.domain.model.DayPlan

interface PlanRepository {
    fun load(): DayPlan
    fun save(plan: DayPlan)
    fun clear()
}
