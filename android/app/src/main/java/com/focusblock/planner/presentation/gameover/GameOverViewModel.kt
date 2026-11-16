package com.focusblock.planner.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusblock.planner.domain.model.DayPlan
import com.focusblock.planner.domain.model.DaySummary
import com.focusblock.planner.domain.repository.PlanRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameOverViewModel(private val planRepository: PlanRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(GameOverUiState())
    val uiState: StateFlow<GameOverUiState> = _uiState.asStateFlow()

    private val replayChannel = Channel<Unit>(Channel.BUFFERED)
    val replayEvents = replayChannel.receiveAsFlow()

    fun bind(summary: DaySummary) {
        _uiState.update { it.copy(loading = false, summary = summary) }
    }

    fun planAgain() {
        viewModelScope.launch {
            val plan = planRepository.load()
            planRepository.save(
                DayPlan(plan.blocks.map { it.copy(done = false, focusedMinutes = 0) })
            )
            replayChannel.send(Unit)
        }
    }
}
