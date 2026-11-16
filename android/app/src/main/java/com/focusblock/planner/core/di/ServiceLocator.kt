package com.focusblock.planner.core.di

import android.content.Context
import com.focusblock.planner.data.local.PreferencesStorage
import com.focusblock.planner.data.repository.CategoryRepositoryImpl
import com.focusblock.planner.data.repository.PlanRepositoryImpl
import com.focusblock.planner.data.repository.SettingsRepositoryImpl
import com.focusblock.planner.data.repository.StatsRepositoryImpl
import com.focusblock.planner.domain.repository.CategoryRepository
import com.focusblock.planner.domain.repository.PlanRepository
import com.focusblock.planner.domain.repository.SettingsRepository
import com.focusblock.planner.domain.repository.StatsRepository
import com.focusblock.planner.domain.usecase.BuildDaySummaryUseCase
import com.focusblock.planner.domain.usecase.BuildWeekStatsUseCase
import com.focusblock.planner.domain.usecase.CompleteBlockUseCase
import com.focusblock.planner.domain.usecase.MoveBlockUseCase
import com.focusblock.planner.domain.usecase.PlaceBlockUseCase

object ServiceLocator {

    private var ready = false

    lateinit var planRepository: PlanRepository
        private set

    lateinit var categoryRepository: CategoryRepository
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var statsRepository: StatsRepository
        private set

    lateinit var placeBlock: PlaceBlockUseCase
        private set

    lateinit var moveBlock: MoveBlockUseCase
        private set

    lateinit var completeBlock: CompleteBlockUseCase
        private set

    lateinit var buildDaySummary: BuildDaySummaryUseCase
        private set

    lateinit var buildWeekStats: BuildWeekStatsUseCase
        private set

    fun init(context: Context) {
        if (ready) return
        val storage = PreferencesStorage(context)
        categoryRepository = CategoryRepositoryImpl(storage)
        planRepository = PlanRepositoryImpl(storage)
        settingsRepository = SettingsRepositoryImpl(storage)
        statsRepository = StatsRepositoryImpl(storage, categoryRepository)
        placeBlock = PlaceBlockUseCase()
        moveBlock = MoveBlockUseCase(placeBlock)
        completeBlock = CompleteBlockUseCase()
        buildDaySummary = BuildDaySummaryUseCase()
        buildWeekStats = BuildWeekStatsUseCase()
        ready = true
    }
}
