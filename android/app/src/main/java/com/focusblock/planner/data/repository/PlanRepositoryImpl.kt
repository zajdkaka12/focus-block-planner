package com.focusblock.planner.data.repository

import com.focusblock.planner.data.local.PreferencesStorage
import com.focusblock.planner.data.sample.SampleData
import com.focusblock.planner.domain.model.DayPlan
import com.focusblock.planner.domain.model.FocusBlock
import com.focusblock.planner.domain.repository.PlanRepository

private const val KEY_PLAN = "day_plan"
private const val FIELD = "\u0001"
private const val ROW = "\u0002"

class PlanRepositoryImpl(private val storage: PreferencesStorage) : PlanRepository {

    override fun load(): DayPlan {
        if (!storage.contains(KEY_PLAN)) {
            val seeded = DayPlan(SampleData.todayBlocks)
            save(seeded)
            return seeded
        }
        val raw = storage.readString(KEY_PLAN, "")
        if (raw.isBlank()) return DayPlan()
        val blocks = raw.split(ROW).mapNotNull { decode(it) }.sortedBy { it.startMinutes }
        return DayPlan(blocks)
    }

    override fun save(plan: DayPlan) {
        storage.writeString(KEY_PLAN, plan.blocks.joinToString(ROW) { encode(it) })
    }

    override fun clear() {
        storage.writeString(KEY_PLAN, "")
    }

    private fun encode(block: FocusBlock): String = listOf(
        block.id.toString(),
        sanitize(block.title),
        block.categoryId,
        block.startMinutes.toString(),
        block.lengthMinutes.toString(),
        if (block.done) "1" else "0",
        block.focusedMinutes.toString()
    ).joinToString(FIELD)

    private fun decode(row: String): FocusBlock? {
        val parts = row.split(FIELD)
        if (parts.size < 7) return null
        return FocusBlock(
            id = parts[0].toLongOrNull() ?: return null,
            title = parts[1],
            categoryId = parts[2],
            startMinutes = parts[3].toIntOrNull() ?: return null,
            lengthMinutes = parts[4].toIntOrNull() ?: return null,
            done = parts[5] == "1",
            focusedMinutes = parts[6].toIntOrNull() ?: 0
        )
    }

    private fun sanitize(value: String): String =
        value.replace(FIELD, " ").replace(ROW, " ").trim()
}
