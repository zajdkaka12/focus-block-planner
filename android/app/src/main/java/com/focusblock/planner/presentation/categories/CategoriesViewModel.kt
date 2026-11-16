package com.focusblock.planner.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.focusblock.planner.data.sample.SampleData
import com.focusblock.planner.domain.model.Category
import com.focusblock.planner.domain.repository.CategoryRepository
import com.focusblock.planner.domain.repository.StatsRepository
import com.focusblock.planner.domain.usecase.BuildWeekStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository,
    private val statsRepository: StatsRepository,
    private val buildWeekStats: BuildWeekStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val categories = categoryRepository.load()
            val stats = buildWeekStats(statsRepository.week())
            _uiState.update { current ->
                current.copy(
                    loading = false,
                    items = categories.map { category ->
                        CategoryItem(
                            category = category,
                            weekMinutes = stats.minutesByCategory[category.id] ?: 0
                        )
                    },
                    palette = SampleData.palette
                )
            }
        }
    }

    fun toggleExpanded(categoryId: String) {
        _uiState.update {
            it.copy(expandedId = if (it.expandedId == categoryId) null else categoryId)
        }
    }

    fun changeColor(categoryId: String, colorArgb: Long) {
        val updated = _uiState.value.items.map { item ->
            if (item.category.id == categoryId) {
                item.copy(category = item.category.copy(colorArgb = colorArgb))
            } else {
                item
            }
        }
        _uiState.update { it.copy(items = updated) }
        persist(updated.map { it.category })
    }

    fun addCategory() {
        val current = _uiState.value.items
        val used = current.map { it.category.name }
        val name = SampleData.extraCategoryNames.firstOrNull { it !in used }
            ?: SampleData.extraCategoryNames.first()
        val color = SampleData.palette[current.size % SampleData.palette.size]
        val category = Category(
            id = "cat_" + System.currentTimeMillis(),
            name = name,
            colorArgb = color,
            spriteIndex = (current.size % 4) + 1
        )
        val updated = current + CategoryItem(category, 0)
        _uiState.update { it.copy(items = updated, expandedId = category.id) }
        persist(updated.map { it.category })
    }

    fun requestDelete(categoryId: String) {
        _uiState.update { it.copy(pendingDeleteId = categoryId) }
    }

    fun dismissDelete() {
        _uiState.update { it.copy(pendingDeleteId = null) }
    }

    fun confirmDelete() {
        val target = _uiState.value.pendingDeleteId ?: return
        val updated = _uiState.value.items.filterNot { it.category.id == target }
        _uiState.update {
            it.copy(items = updated, pendingDeleteId = null, expandedId = null)
        }
        persist(updated.map { it.category })
    }

    private fun persist(categories: List<Category>) {
        viewModelScope.launch { categoryRepository.save(categories) }
    }
}
