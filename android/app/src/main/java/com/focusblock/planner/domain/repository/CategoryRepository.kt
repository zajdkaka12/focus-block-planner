package com.focusblock.planner.domain.repository

import com.focusblock.planner.domain.model.Category

interface CategoryRepository {
    fun load(): List<Category>
    fun save(categories: List<Category>)
}
