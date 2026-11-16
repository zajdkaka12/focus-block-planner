package com.focusblock.planner.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusblock.planner.domain.repository.CategoryRepository
import com.focusblock.planner.domain.repository.PlanRepository
import com.focusblock.planner.domain.repository.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel(
    private val planRepository: PlanRepository,
    private val categoryRepository: CategoryRepository,
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val plan = planRepository.load()
            val categories = categoryRepository.load()
            val week = statsRepository.week()
            _uiState.update {
                it.copy(
                    loading = false,
                    blockCount = plan.blocks.size,
                    plannedMinutes = plan.plannedMinutes,
                    focusedMinutes = plan.focusedMinutes,
                    streak = statsRepository.streak(),
                    categories = categories,
                    weekMinutes = week.map { record -> record.totalMinutes }
                )
            }
        }
    }
}
