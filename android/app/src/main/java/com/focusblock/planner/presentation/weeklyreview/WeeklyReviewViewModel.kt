package com.focusblock.planner.presentation.weeklyreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusblock.planner.domain.repository.CategoryRepository
import com.focusblock.planner.domain.repository.StatsRepository
import com.focusblock.planner.domain.usecase.BuildWeekStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeeklyReviewViewModel(
    private val categoryRepository: CategoryRepository,
    private val statsRepository: StatsRepository,
    private val buildWeekStats: BuildWeekStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyReviewUiState())
    val uiState: StateFlow<WeeklyReviewUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val categories = categoryRepository.load()
            val stats = buildWeekStats(statsRepository.week())
            _uiState.update {
                it.copy(loading = false, stats = stats, categories = categories)
            }
        }
    }
}
