package com.focusblock.planner.presentation.weeklyreview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusblock.planner.R
import com.focusblock.planner.core.di.AppViewModelFactory
import com.focusblock.planner.core.theme.Accent
import com.focusblock.planner.core.theme.CatHealth
import com.focusblock.planner.core.theme.CatStudy
import com.focusblock.planner.core.theme.DividerSoft
import com.focusblock.planner.core.theme.TextFaint
import com.focusblock.planner.core.theme.TextMuted
import com.focusblock.planner.core.theme.TextPrimary
import com.focusblock.planner.core.theme.TextSecondary
import com.focusblock.planner.core.ui.BentoTile
import com.focusblock.planner.core.ui.EmptyStateCard
import com.focusblock.planner.core.ui.IconTile
import com.focusblock.planner.core.ui.ScreenScaffold
import com.focusblock.planner.core.ui.SectionHeader
import com.focusblock.planner.core.ui.StatCard
import com.focusblock.planner.core.ui.argbColor
import com.focusblock.planner.core.ui.durationText
import com.focusblock.planner.core.ui.weekdayInitial
import com.focusblock.planner.core.ui.weekdayName

private val WeeklyOverlay = listOf(
    Color(0xE6141022),
    Color(0xEB231846),
    Color(0xF5141022)
)

@Composable
fun WeeklyReviewRoute(onBack: () -> Unit) {
    val weeklyViewModel: WeeklyReviewViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by weeklyViewModel.uiState.collectAsStateWithLifecycle()
    DisposableEffect(Unit) {
        weeklyViewModel.refresh()
        onDispose { }
    }
    WeeklyReviewScreen(state = state, onBack = onBack)
}

@Composable
private fun WeeklyReviewScreen(state: WeeklyReviewUiState, onBack: () -> Unit) {
    ScreenScaffold(overlay = WeeklyOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            SectionHeader(
                eyebrow = stringResource(R.string.weekly_eyebrow),
                title = stringResource(R.string.weekly_title),
                modifier = Modifier.fillMaxWidth(),
                leading = {
                    IconTile(
                        contentDescription = stringResource(R.string.cd_back),
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(18.dp))
            if (state.isEmpty) {
                EmptyStateCard(
                    message = stringResource(R.string.weekly_empty),
                    spriteRes = R.drawable.sprite_5,
                    spriteDescription = stringResource(R.string.cd_hero),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                TotalsRow(state = state)
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = stringResource(R.string.weekly_hours),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(10.dp))
                CategoryGrid(state = state)
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = stringResource(R.string.weekly_strip),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(10.dp))
                WeekStrip(state = state)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TotalsRow(state: WeeklyReviewUiState) {
    val bestIndex = state.stats.bestDayIndex
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            value = durationText(state.stats.totalMinutes),
            label = stringResource(R.string.stat_focused),
            accent = CatStudy,
            live = true,
            modifier = Modifier.weight(1f)
        )
        if (state.stats.blocksDone > 0) {
            StatCard(
                value = state.stats.blocksDone.toString(),
                label = stringResource(R.string.weekly_blocks_done),
                accent = CatHealth,
                modifier = Modifier.weight(1f)
            )
        }
        if (bestIndex >= 0) {
            StatCard(
                value = weekdayName(bestIndex),
                label = stringResource(R.string.weekly_best_day),
                accent = Accent,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CategoryGrid(state: WeeklyReviewUiState) {
    val peak = state.maxCategoryMinutes.coerceAtLeast(1)
    val rows = state.categories.take(4).chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { category ->
                    val minutes = state.stats.minutesByCategory[category.id] ?: 0
                    val color = argbColor(category.colorArgb)
                    BentoTile(modifier = Modifier.weight(1f).height(112.dp)) {
                        Text(
                            text = category.name.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = durationText(minutes),
                            style = MaterialTheme.typography.headlineMedium,
                            color = color,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(DividerSoft)
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth((minutes.toFloat() / peak).coerceAtLeast(0.02f))
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(color)
                            )
                        }
                    }
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun WeekStrip(state: WeeklyReviewUiState) {
    val peak = state.maxDayMinutes.coerceAtLeast(1)
    BentoTile(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().height(140.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.stats.days.forEachIndexed { index, record ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (record.totalMinutes > 0) {
                        Text(
                            text = durationText(record.totalMinutes),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                letterSpacing = 0.4.sp
                            ),
                            color = TextFaint,
                            maxLines = 1
                        )
                    } else {
                        Spacer(modifier = Modifier.height(11.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier
                            .width(18.dp)
                            .height((record.totalMinutes * 92 / peak).coerceAtLeast(4).dp)
                            .clip(RoundedCornerShape(5.dp)),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        state.categories.take(4).forEach { category ->
                            val minutes = record.minutesByCategory[category.id] ?: 0
                            if (minutes > 0) {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(minutes.toFloat())
                                        .background(argbColor(category.colorArgb))
                                )
                            }
                        }
                        if (record.totalMinutes == 0) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(DividerSoft)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = weekdayInitial(index),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (index == state.stats.bestDayIndex) TextPrimary else TextFaint,
                        fontWeight = if (index == state.stats.bestDayIndex) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Medium
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
