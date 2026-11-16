package com.focusblock.planner.presentation.categories

import com.focusblock.planner.domain.model.Category

data class CategoryItem(
    val category: Category,
    val weekMinutes: Int
)

data class CategoriesUiState(
    val loading: Boolean = true,
    val items: List<CategoryItem> = emptyList(),
    val expandedId: String? = null,
    val pendingDeleteId: String? = null,
    val palette: List<Long> = emptyList()
) {
    val isEmpty: Boolean
        get() = !loading && items.isEmpty()

    val pendingDeleteName: String
        get() = items.firstOrNull { it.category.id == pendingDeleteId }?.category?.name.orEmpty()
}
