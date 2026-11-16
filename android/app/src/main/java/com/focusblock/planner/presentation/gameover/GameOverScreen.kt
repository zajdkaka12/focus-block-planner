package com.focusblock.planner.presentation.gameover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
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
import com.focusblock.planner.core.theme.PrimaryBright
import com.focusblock.planner.core.theme.TextMuted
import com.focusblock.planner.core.theme.TextPrimary
import com.focusblock.planner.core.theme.TextSecondary
import com.focusblock.planner.core.ui.BarSegment
import com.focusblock.planner.core.ui.BentoTile
import com.focusblock.planner.core.ui.CategoryBar
import com.focusblock.planner.core.ui.PrimaryButton
import com.focusblock.planner.core.ui.ScreenScaffold
import com.focusblock.planner.core.ui.SecondaryButton
import com.focusblock.planner.core.ui.StatCard
import com.focusblock.planner.core.ui.argbColor
import com.focusblock.planner.core.ui.countText
import com.focusblock.planner.core.ui.durationText
import com.focusblock.planner.domain.model.DaySummary

private val OverOverlay = listOf(
    Color(0xF2141022),
    Color(0xD9241744),
    Color(0xF7141022)
)

private enum class OverStat { Focused, Blocks, Streak, TopCategory }

@Composable
fun GameOverRoute(
    summary: DaySummary,
    onPlanAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val gameOverViewModel: GameOverViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by gameOverViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(summary) {
        gameOverViewModel.bind(summary)
    }
    LaunchedEffect(Unit) {
        gameOverViewModel.replayEvents.collect { onPlanAgain() }
    }
    GameOverScreen(
        state = state,
        onPlanAgain = gameOverViewModel::planAgain,
        onMenu = onMenu
    )
}

@Composable
private fun GameOverScreen(
    state: GameOverUiState,
    onPlanAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val summary = state.summary
    val heroState = remember { MutableTransitionState(false).apply { targetState = true } }
    val transition = rememberInfinite()
    val visibleStats = statsFor(summary)
    ScreenScaffold(
        overlay = OverOverlay,
        decor = {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(360.dp)
                    .background(
                        Brush.radialGradient(listOf(Accent.copy(alpha = 0.16f), Color.Transparent))
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            AnimatedVisibility(
                visibleState = heroState,
                enter = scaleIn(spring(dampingRatio = 0.6f)) + fadeIn(tween(240))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .alpha(transition)
                            .background(
                                Brush.radialGradient(
                                    listOf(Accent.copy(alpha = 0.5f), Color.Transparent)
                                )
                            )
                    )
                    Image(
                        painter = painterResource(R.drawable.sprite_5),
                        contentDescription = stringResource(R.string.cd_hero),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(104.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.over_title),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (summary.focusedMinutes > 0) {
                    stringResource(
                        R.string.over_subtitle,
                        durationText(summary.focusedMinutes)
                    )
                } else {
                    stringResource(R.string.over_subtitle_empty)
                },
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            if (summary.newBest) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.over_new_best),
                    style = MaterialTheme.typography.labelSmall,
                    color = Accent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            if (visibleStats.size < 2) {
                BentoTile(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.over_summary_card),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    visibleStats.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            row.forEach { stat ->
                                OverStatCard(
                                    stat = stat,
                                    summary = summary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            if (summary.segments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = stringResource(R.string.over_split),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                CategoryBar(
                    segments = summary.segments.map {
                        BarSegment(argbColor(it.colorArgb), it.minutes)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    summary.segments.take(4).forEach { segment ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(argbColor(segment.colorArgb))
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = segment.name.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                ),
                                color = TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            PrimaryButton(
                text = stringResource(R.string.cta_plan_again),
                onClick = onPlanAgain,
                icon = Icons.Default.Refresh,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            SecondaryButton(
                text = stringResource(R.string.cta_menu),
                onClick = onMenu,
                modifier = Modifier.fillMaxWidth(),
                leading = {
                    Image(
                        painter = painterResource(R.drawable.icon_home),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun OverStatCard(
    stat: OverStat,
    summary: DaySummary,
    modifier: Modifier = Modifier
) {
    when (stat) {
        OverStat.Focused -> StatCard(
            value = durationText(summary.focusedMinutes),
            label = stringResource(R.string.stat_focused),
            accent = CatStudy,
            live = true,
            modifier = modifier
        )
        OverStat.Blocks -> StatCard(
            value = countText(summary.blocksDone, summary.blocksPlanned),
            label = stringResource(R.string.stat_blocks),
            accent = CatHealth,
            modifier = modifier
        )
        OverStat.Streak -> StatCard(
            value = stringResource(R.string.fmt_days, summary.bestStreak),
            label = stringResource(R.string.stat_best_streak),
            accent = Accent,
            modifier = modifier
        )
        OverStat.TopCategory -> StatCard(
            value = summary.topCategoryName.uppercase(),
            label = stringResource(R.string.stat_top_category),
            accent = PrimaryBright,
            modifier = modifier
        )
    }
}

private fun statsFor(summary: DaySummary): List<OverStat> {
    val result = mutableListOf<OverStat>()
    if (summary.focusedMinutes > 0) result.add(OverStat.Focused)
    if (summary.blocksDone > 0 && summary.blocksPlanned > 0) result.add(OverStat.Blocks)
    if (summary.bestStreak > 0) result.add(OverStat.Streak)
    if (summary.topCategoryName.isNotBlank()) result.add(OverStat.TopCategory)
    return result
}

@Composable
private fun rememberInfinite(): Float {
    val transition = rememberInfiniteTransition(label = "heroGlow")
    val value by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroGlowAlpha"
    )
    return value
}
