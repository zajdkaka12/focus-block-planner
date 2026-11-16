package com.focusblock.planner.data.repository

import com.focusblock.planner.core.config.GameConfig
import com.focusblock.planner.data.local.PreferencesStorage
import com.focusblock.planner.data.sample.SampleData
import com.focusblock.planner.domain.model.DayRecord
import com.focusblock.planner.domain.repository.CategoryRepository
import com.focusblock.planner.domain.repository.StatsRepository
import java.util.Calendar

private const val KEY_SEEDED = "week_seeded"
private const val KEY_DAY_PREFIX = "week_day_"
private const val KEY_STREAK = "streak"
private const val KEY_BEST_STREAK = "best_streak"
private const val FIELD = "\u0001"
private const val PAIR = "\u0002"
private const val KEY_VALUE = "\u0003"

class StatsRepositoryImpl(
    private val storage: PreferencesStorage,
    private val categoryRepository: CategoryRepository
) : StatsRepository {

    override fun week(): List<DayRecord> {
        if (!storage.contains(KEY_SEEDED)) {
            seed()
            storage.writeBoolean(KEY_SEEDED, true)
        }
        return (0 until GameConfig.WEEK_DAYS).map { index ->
            decode(index, storage.readString(KEY_DAY_PREFIX + index, ""))
        }
    }

    override fun recordToday(blocksDone: Int, minutesByCategory: Map<String, Int>) {
        val index = todayIndex()
        val existing = decode(index, storage.readString(KEY_DAY_PREFIX + index, ""))
        val merged = existing.minutesByCategory.toMutableMap()
        minutesByCategory.forEach { entry ->
            merged[entry.key] = maxOf(merged[entry.key] ?: 0, entry.value)
        }
        val record = DayRecord(
            dayIndex = index,
            blocksDone = maxOf(existing.blocksDone, blocksDone),
            minutesByCategory = merged
        )
        storage.writeString(KEY_DAY_PREFIX + index, encode(record))
    }

    override fun streak(): Int = storage.readInt(KEY_STREAK, 0)

    override fun bestStreak(): Int = storage.readInt(KEY_BEST_STREAK, 0)

    override fun saveStreak(streak: Int, best: Int) {
        storage.writeInt(KEY_STREAK, streak)
        storage.writeInt(KEY_BEST_STREAK, best)
    }

    override fun todayIndex(): Int {
        val calendar = Calendar.getInstance()
        return (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
    }

    override fun nowMinutes(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    }

    private fun seed() {
        val categories = categoryRepository.load()
        if (categories.isEmpty()) return
        val today = todayIndex()
        SampleData.weekMinutes.forEachIndexed { index, total ->
            if (index != today) {
                val minutes = mutableMapOf<String, Int>()
                categories.take(SampleData.weekShares.size).forEachIndexed { position, category ->
                    val value = (total * SampleData.weekShares[position]).toInt()
                    if (value > 0) minutes[category.id] = value
                }
                val record = DayRecord(
                    dayIndex = index,
                    blocksDone = SampleData.weekBlocksDone.getOrElse(index) { 0 },
                    minutesByCategory = minutes
                )
                storage.writeString(KEY_DAY_PREFIX + index, encode(record))
            }
        }
    }

    private fun encode(record: DayRecord): String {
        val pairs = record.minutesByCategory.entries.joinToString(PAIR) { entry ->
            entry.key + KEY_VALUE + entry.value.toString()
        }
        return listOf(record.blocksDone.toString(), pairs).joinToString(FIELD)
    }

    private fun decode(index: Int, raw: String): DayRecord {
        if (raw.isBlank()) return DayRecord(index)
        val parts = raw.split(FIELD)
        val blocksDone = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val minutes = mutableMapOf<String, Int>()
        parts.getOrNull(1)?.split(PAIR)?.forEach { entry ->
            val split = entry.split(KEY_VALUE)
            val value = split.getOrNull(1)?.toIntOrNull()
            if (split.size == 2 && value != null && value > 0) {
                minutes[split[0]] = value
            }
        }
        return DayRecord(index, blocksDone, minutes)
    }
}
