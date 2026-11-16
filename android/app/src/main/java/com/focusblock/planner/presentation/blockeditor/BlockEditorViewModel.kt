package com.focusblock.planner.presentation.blockeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusblock.planner.core.config.GameConfig
import com.focusblock.planner.domain.model.DayPlan
import com.focusblock.planner.domain.model.FocusBlock
import com.focusblock.planner.domain.repository.CategoryRepository
import com.focusblock.planner.domain.repository.PlanRepository
import com.focusblock.planner.domain.repository.SettingsRepository
import com.focusblock.planner.domain.usecase.PlaceBlockUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BlockEditorViewModel(
    private val planRepository: PlanRepository,
    private val categoryRepository: CategoryRepository,
    private val settingsRepository: SettingsRepository,
    private val placeBlock: PlaceBlockUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BlockEditorUiState())
    val uiState: StateFlow<BlockEditorUiState> = _uiState.asStateFlow()

    private val closeChannel = Channel<Unit>(Channel.BUFFERED)
    val closeEvents = closeChannel.receiveAsFlow()

    private var blocks: List<FocusBlock> = emptyList()

    fun load(blockId: Long, startMinutes: Int) {
        viewModelScope.launch {
            val settings = settingsRepository.load()
            val categories = categoryRepository.load()
            val plan = planRepository.load()
            blocks = plan.blocks
            val existing = plan.blocks.firstOrNull { it.id == blockId }
            val defaultStart = startMinutes.coerceIn(
                settings.dayStartMinutes,
                settings.dayEndMinutes - GameConfig.MIN_BLOCK_MINUTES
            )
            _uiState.value = BlockEditorUiState(
                loading = false,
                blockId = existing?.id ?: NEW_BLOCK_ID,
                title = existing?.title.orEmpty(),
                categories = categories,
                categoryId = existing?.categoryId ?: categories.firstOrNull()?.id.orEmpty(),
                lengthMinutes = existing?.lengthMinutes ?: settings.defaultBlockMinutes,
                startMinutes = existing?.startMinutes ?: defaultStart,
                dayStartMinutes = settings.dayStartMinutes,
                dayEndMinutes = settings.dayEndMinutes,
                editing = existing != null,
                hapticsEnabled = settings.hapticsEnabled
            )
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value.take(60), showTitleError = false) }
    }

    fun onCategoryChange(categoryId: String) {
        _uiState.update { it.copy(categoryId = categoryId) }
    }

    fun changeLength(delta: Int) {
        _uiState.update {
            val next = (it.lengthMinutes + delta).coerceIn(
                GameConfig.MIN_BLOCK_MINUTES,
                GameConfig.MAX_BLOCK_MINUTES
            )
            it.copy(lengthMinutes = next, showFitError = false)
        }
    }

    fun changeStart(delta: Int) {
        _uiState.update {
            val next = (it.startMinutes + delta).coerceIn(
                it.dayStartMinutes,
                it.dayEndMinutes - GameConfig.MIN_BLOCK_MINUTES
            )
            it.copy(startMinutes = next, showFitError = false)
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(showTitleError = true) }
            return
        }
        val existing = blocks.firstOrNull { it.id == state.blockId }
        val candidate = FocusBlock(
            id = if (state.editing) state.blockId else nextId(),
            title = state.title.trim(),
            categoryId = state.categoryId,
            startMinutes = state.startMinutes,
            lengthMinutes = state.lengthMinutes,
            done = existing?.done ?: false,
            focusedMinutes = existing?.focusedMinutes ?: 0
        )
        val updated = placeBlock(
            blocks,
            candidate,
            state.dayStartMinutes,
            state.dayEndMinutes
        )
        if (updated == null) {
            _uiState.update { it.copy(showFitError = true) }
            return
        }
        blocks = updated
        viewModelScope.launch {
            planRepository.save(DayPlan(updated))
            _uiState.update { it.copy(confirmTick = it.confirmTick + 1) }
            delay(SAVE_FEEDBACK_MS)
            closeChannel.send(Unit)
        }
    }

    fun delete() {
        val state = _uiState.value
        if (!state.editing) return
        val updated = blocks.filterNot { it.id == state.blockId }
        blocks = updated
        viewModelScope.launch {
            planRepository.save(DayPlan(updated))
            closeChannel.send(Unit)
        }
    }

    private fun nextId(): Long = (blocks.maxOfOrNull { it.id } ?: 0L) + 1L

    companion object {
        const val NEW_BLOCK_ID = -1L
        private const val SAVE_FEEDBACK_MS = 420L
    }
}
