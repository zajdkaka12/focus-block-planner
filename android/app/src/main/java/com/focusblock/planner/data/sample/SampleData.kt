package com.focusblock.planner.data.sample

import com.focusblock.planner.domain.model.Category
import com.focusblock.planner.domain.model.FocusBlock

object SampleData {

    val categories = listOf(
        Category(id = "work", name = "Work", colorArgb = 0xFF6A4AD6, spriteIndex = 1),
        Category(id = "study", name = "Study", colorArgb = 0xFF2FB8D9, spriteIndex = 2),
        Category(id = "home", name = "Home", colorArgb = 0xFFE05028, spriteIndex = 3),
        Category(id = "health", name = "Health", colorArgb = 0xFF4FD1A1, spriteIndex = 4)
    )

    val todayBlocks = listOf(
        FocusBlock(1L, "Deep work: API refactor", "work", 540, 90),
        FocusBlock(2L, "Read chapter 4", "study", 660, 50),
        FocusBlock(3L, "Inbox zero", "home", 840, 30),
        FocusBlock(4L, "Evening walk", "health", 1080, 40)
    )

    val weekMinutes = listOf(190, 160, 240, 110, 210, 80, 130)

    val weekBlocksDone = listOf(4, 3, 5, 2, 4, 2, 3)

    val weekShares = listOf(0.45f, 0.25f, 0.15f, 0.15f)

    val extraCategoryNames = listOf("Creative", "Admin", "Fitness", "Reading", "Planning")

    val palette = listOf(
        0xFF6A4AD6,
        0xFF2FB8D9,
        0xFFE05028,
        0xFF4FD1A1,
        0xFFD9A62F,
        0xFFD94F8A
    )
}
