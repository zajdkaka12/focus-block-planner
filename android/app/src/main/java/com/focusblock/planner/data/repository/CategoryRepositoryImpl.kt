package com.focusblock.planner.data.repository

import com.focusblock.planner.data.local.PreferencesStorage
import com.focusblock.planner.data.sample.SampleData
import com.focusblock.planner.domain.model.Category
import com.focusblock.planner.domain.repository.CategoryRepository

private const val KEY_CATEGORIES = "categories"
private const val FIELD = "\u0001"
private const val ROW = "\u0002"

class CategoryRepositoryImpl(private val storage: PreferencesStorage) : CategoryRepository {

    override fun load(): List<Category> {
        if (!storage.contains(KEY_CATEGORIES)) {
            save(SampleData.categories)
            return SampleData.categories
        }
        val raw = storage.readString(KEY_CATEGORIES, "")
        if (raw.isBlank()) return emptyList()
        return raw.split(ROW).mapNotNull { decode(it) }
    }

    override fun save(categories: List<Category>) {
        storage.writeString(KEY_CATEGORIES, categories.joinToString(ROW) { encode(it) })
    }

    private fun encode(category: Category): String = listOf(
        category.id,
        category.name,
        category.colorArgb.toString(),
        category.spriteIndex.toString()
    ).joinToString(FIELD)

    private fun decode(row: String): Category? {
        val parts = row.split(FIELD)
        if (parts.size < 4) return null
        val color = parts[2].toLongOrNull() ?: return null
        val sprite = parts[3].toIntOrNull() ?: return null
        return Category(parts[0], parts[1], color, sprite)
    }
}
