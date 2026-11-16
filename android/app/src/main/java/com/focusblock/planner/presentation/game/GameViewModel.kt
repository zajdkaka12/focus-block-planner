package com.focusblock.planner.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusblock.planner.core.config.GameConfig
import com.focusblock.planner.domain.model.DayPlan
import com.focusblock.planner.domain.model.FocusBlock
import com.focusblock.planner.domain.repository.CategoryRepository
import com.focusblock.planner.domain.repository.PlanRepository
import com.focusblock.planner.domain.repository.SettingsRepository
import com.focusblock.planner.domain.repository.StatsRepository
import com.focusblock.planner.domain.usecase.BuildDaySummaryUseCase
import com.focusblock.planner.domain.usecase.CompleteBlockUseCase
import com.focusblock.planner.domain.usecase.MoveBlockUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(
    private val planRepository: PlanRepository,
    private val categoryRepository: CategoryRepository,
    private val settingsRepository: SettingsRepository,
    private val statsRepository: StatsRepository,
    private val moveBlock: MoveBlockUseCase,
    private val completeBlock: CompleteBlockUseCase,
    private val buildDaySummary: BuildDaySummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private var timerJob: Job? = null
    private var errorJob: Job? = null
    private var autoStartNext = false

    fun refresh() {
        viewModelScope.launch {
            val settings = settingsRepository.load()
            val plan = planRepository.load()
            val categories = categoryRepository.load()
            autoStartNext = settings.autoStartNext
            _uiState.update { current ->
                current.copy(
                    loading = false,
                    blocks = plan.blocks,
                    categories = categories,
                    dayStartMinutes = settings.dayStartMinutes,
                    dayEndMinutes = settings.dayEndMinutes,
                    nowMinutes = statsRepository.nowMinutes(),
                    selectedCategoryId = current.selectedCategoryId.ifBlank {
                        categories.firstOrNull()?.id.orEmpty()
                    },
                    selectedBlockId = current.selectedBlockId
                        ?.takeIf { id -> plan.blocks.any { it.id == id } }
                )
            }
        }
    }

    fun selectBlock(blockId: Long) {
        if (_uiState.value.mode == GameMode.Running) return
        _uiState.update {
            it.copy(selectedBlockId = if (it.selectedBlockId == blockId) null else blockId)
        }
    }

    fun selectCategory(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun requestNewBlock(startMinutes: Int) {
        if (_uiState.value.mode == GameMode.Running) return
        viewModelScope.launch {
            eventChannel.send(GameEvent.OpenEditor(NEW_BLOCK_ID, startMinutes))
        }
    }

    fun requestEditSelected() {
        val selected = _uiState.value.selectedBlockId ?: return
        val block = _uiState.value.blocks.firstOrNull { it.id == selected } ?: return
        viewModelScope.launch {
            eventChannel.send(GameEvent.OpenEditor(block.id, block.startMinutes))
        }
    }

    fun moveBlockBySlots(blockId: Long, slotDelta: Int) {
        if (slotDelta == 0) return
        val state = _uiState.value
        val block = state.blocks.firstOrNull { it.id == blockId } ?: return
        val target = block.startMinutes + slotDelta * GameConfig.SLOT_MINUTES
        val updated = moveBlock(
            state.blocks,
            blockId,
            target,
            state.dayStartMinutes,
            state.dayEndMinutes
        )
        if (updated == null) {
            showError()
            return
        }
        persist(updated)
    }

    fun startSelectedBlock() {
        val state = _uiState.value
        val block = state.blocks.firstOrNull { it.id == state.selectedBlockId } ?: return
        if (block.done) return
        startBlock(block.id, block.lengthMinutes)
    }

    fun togglePause() {
        val state = _uiState.value
        if (state.mode != GameMode.Running) return
        if (state.paused) {
            _uiState.update { it.copy(paused = false) }
            runTimer()
        } else {
            timerJob?.cancel()
            timerJob = null
            _uiState.update { it.copy(paused = true) }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        if (_uiState.value.mode == GameMode.Running) {
            _uiState.update { it.copy(paused = true) }
        }
    }

    fun completeActiveBlock() {
        val state = _uiState.value
        val block = state.activeBlock ?: return
        val elapsedSeconds = (state.totalSeconds - state.remainingSeconds).coerceAtLeast(0)
        finishBlock(block.id, elapsedSeconds / 60, completedEarly = state.remainingSeconds > 0)
    }

    fun cancelActiveBlock() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update {
            it.copy(
                mode = GameMode.Planning,
                activeBlockId = null,
                remainingSeconds = 0,
                totalSeconds = 1,
                paused = false
            )
        }
    }

    fun finishDay() {
        timerJob?.cancel()
        timerJob = null
        viewModelScope.launch {
            val state = _uiState.value
            val plan = DayPlan(state.blocks)
            val previousStreak = statsRepository.streak()
            val storedBest = statsRepository.bestStreak()
            val summary = buildDaySummary(plan, state.categories, previousStreak, storedBest)
            statsRepository.saveStreak(summary.streak, summary.bestStreak)
            statsRepository.recordToday(
                blocksDone = summary.blocksDone,
                minutesByCategory = buildDaySummary.minutesByCategory(plan)
            )
            _uiState.update {
                it.copy(
                    mode = GameMode.Planning,
                    activeBlockId = null,
                    remainingSeconds = 0,
                    totalSeconds = 1,
                    paused = false
                )
            }
            eventChannel.send(GameEvent.DayComplete(summary))
        }
    }

    private fun startBlock(blockId: Long, lengthMinutes: Int) {
        val seconds = (lengthMinutes * 60).coerceAtLeast(1)
        _uiState.update {
            it.copy(
                mode = GameMode.Running,
                activeBlockId = blockId,
                selectedBlockId = blockId,
                remainingSeconds = seconds,
                totalSeconds = seconds,
                paused = false
            )
        }
        runTimer()
    }

    private fun runTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _uiState.value.remainingSeconds > 0) {
                delay(GameConfig.TICK_MS)
                val next = _uiState.value.remainingSeconds - 1
                _uiState.update { it.copy(remainingSeconds = next.coerceAtLeast(0)) }
                if (next <= 0) {
                    val active = _uiState.value.activeBlockId
                    if (active != null) {
                        finishBlock(active, _uiState.value.totalSeconds / 60, completedEarly = false)
                    }
                }
            }
        }
    }

    private fun finishBlock(blockId: Long, elapsedMinutes: Int, completedEarly: Boolean) {
        timerJob?.cancel()
        timerJob = null
        val updated = completeBlock(_uiState.value.blocks, blockId, elapsedMinutes, completedEarly)
        _uiState.update {
            it.copy(
                blocks = updated,
                mode = GameMode.Planning,
                activeBlockId = null,
                remainingSeconds = 0,
                totalSeconds = 1,
                paused = false,
                selectedBlockId = null
            )
        }
        viewModelScope.launch {
            planRepository.save(DayPlan(updated))
            statsRepository.recordToday(
                blocksDone = updated.count { it.done },
                minutesByCategory = buildDaySummary.minutesByCategory(DayPlan(updated))
            )
        }
        val pending = updated.filterNot { it.done }
        if (pending.isEmpty()) {
            finishDay()
        } else if (autoStartNext) {
            val next = pending.minByOrNull { it.startMinutes }
            if (next != null) startBlock(next.id, next.lengthMinutes)
        }
    }

    private fun persist(blocks: List<FocusBlock>) {
        _uiState.update { it.copy(blocks = blocks) }
        viewModelScope.launch { planRepository.save(DayPlan(blocks)) }
    }

    private fun showError() {
        errorJob?.cancel()
        _uiState.update { it.copy(errorVisible = true) }
        errorJob = viewModelScope.launch {
            delay(GameConfig.ERROR_VISIBLE_MS)
            _uiState.update { it.copy(errorVisible = false) }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        errorJob?.cancel()
        super.onCleared()
    }

    companion object {
        const val NEW_BLOCK_ID = -1L
    }
}
