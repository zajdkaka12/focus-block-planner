package com.focusblock.planner.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.focusblock.planner.R

@Composable
fun durationText(minutes: Int): String {
    val safe = minutes.coerceAtLeast(0)
    val hours = safe / 60
    val rest = safe % 60
    return when {
        hours > 0 && rest > 0 -> stringResource(R.string.fmt_duration_hm, hours, rest)
        hours > 0 -> stringResource(R.string.fmt_duration_h, hours)
        else -> stringResource(R.string.fmt_duration_m, rest)
    }
}

@Composable
fun timeText(minuteOfDay: Int): String {
    val clamped = minuteOfDay.coerceIn(0, 24 * 60)
    return stringResource(R.string.fmt_time, clamped / 60, clamped % 60)
}

@Composable
fun timeRangeText(startMinutes: Int, endMinutes: Int): String =
    stringResource(R.string.fmt_time_range, timeText(startMinutes), timeText(endMinutes))

@Composable
fun countdownText(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    return stringResource(R.string.fmt_countdown, safe / 60, safe % 60)
}

@Composable
fun countText(done: Int, total: Int): String = stringResource(R.string.fmt_count, done, total)

@Composable
fun weekdayName(index: Int): String = stringResource(
    when (index) {
        0 -> R.string.weekday_0
        1 -> R.string.weekday_1
        2 -> R.string.weekday_2
        3 -> R.string.weekday_3
        4 -> R.string.weekday_4
        5 -> R.string.weekday_5
        else -> R.string.weekday_6
    }
)

@Composable
fun weekdayInitial(index: Int): String = stringResource(
    when (index) {
        0 -> R.string.weekday_initial_0
        1 -> R.string.weekday_initial_1
        2 -> R.string.weekday_initial_2
        3 -> R.string.weekday_initial_3
        4 -> R.string.weekday_initial_4
        5 -> R.string.weekday_initial_5
        else -> R.string.weekday_initial_6
    }
)
