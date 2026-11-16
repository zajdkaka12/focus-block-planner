package com.focusblock.planner.presentation.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusblock.planner.R
import com.focusblock.planner.core.di.AppViewModelFactory
import com.focusblock.planner.core.theme.Accent
import com.focusblock.planner.core.theme.AccentDeep
import com.focusblock.planner.core.theme.AccentSoft
import com.focusblock.planner.core.theme.CatHealth
import com.focusblock.planner.core.theme.CatStudy
import com.focusblock.planner.core.theme.OnAccent
import com.focusblock.planner.core.theme.Primary
import com.focusblock.planner.core.theme.PrimaryBright
import com.focusblock.planner.core.theme.TextFaint
import com.focusblock.planner.core.theme.TextMuted
import com.focusblock.planner.core.theme.TextPrimary
import com.focusblock.planner.core.theme.TextSecondary
import com.focusblock.planner.core.ui.BentoTile
import com.focusblock.planner.core.ui.IconTile
import com.focusblock.planner.core.ui.ScreenScaffold
import com.focusblock.planner.core.ui.StatCard
import com.focusblock.planner.core.ui.durationText

private val MenuOverlay = listOf(
    Color(0xD9141022),
    Color(0xE61B1533),
    Color(0xF2141022)
)

@Composable
fun MenuRoute(
    onStart: () -> Unit,
    onCategories: () -> Unit,
    onWeekly: () -> Unit,
    onSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by menuViewModel.uiState.collectAsStateWithLifecycle()
    DisposableEffect(Unit) {
        menuViewModel.refresh()
        onDispose { }
    }
    MenuScreen(
        state = state,
        onStart = onStart,
        onCategories = onCategories,
        onWeekly = onWeekly,
        onSettings = onSettings
    )
}

@Composable
private fun MenuScreen(
    state: MenuUiState,
    onStart: () -> Unit,
    onCategories: () -> Unit,
    onWeekly: () -> Unit,
    onSettings: () -> Unit
) {
    val gridState = remember { MutableTransitionState(false).apply { targetState = true } }
    ScreenScaffold(
        overlay = MenuOverlay,
        decor = {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(320.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(Primary.copy(alpha = 0.22f), Color.Transparent)
                        )
                    )
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(12.dp))
            MenuHeader(onSettings = onSettings)
            Spacer(modifier = Modifier.height(18.dp))
            MenuHero(state = state)
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visibleState = gridState,
                enter = fadeIn(tween(220)) + slideInVertically { it / 8 },
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.fillMaxWidth().weight(0.62f)) {
                        StartTile(
                            onStart = onStart,
                            modifier = Modifier.weight(0.58f).fillMaxHeight()
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        StatColumn(
                            state = state,
                            modifier = Modifier.weight(0.42f).fillMaxHeight()
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth().weight(0.38f)) {
                        CategoriesTile(
                            count = state.categories.size,
                            onClick = onCategories,
                            modifier = Modifier.weight(0.44f).fillMaxHeight()
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        WeeklyTile(
                            weekMinutes = state.weekMinutes,
                            onClick = onWeekly,
                            modifier = Modifier.weight(0.56f).fillMaxHeight()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.menu_footer),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = TextFaint,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun MenuHeader(onSettings: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.cd_logo),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.brand_line_one),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = stringResource(R.string.brand_line_two),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                maxLines = 1
            )
        }
        IconTile(
            contentDescription = stringResource(R.string.cd_settings),
            onClick = onSettings
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.cd_settings),
                tint = TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun MenuHero(state: MenuUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.menu_today),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.4.sp),
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (state.blockCount == 0) {
                stringResource(R.string.menu_no_plan)
            } else {
                stringResource(
                    R.string.menu_plan_summary,
                    state.blockCount,
                    durationText(state.plannedMinutes)
                )
            },
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            maxLines = 2
        )
    }
}

@Composable
private fun StartTile(onStart: () -> Unit, modifier: Modifier = Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 900f),
        label = "startTile"
    )
    val shape = RoundedCornerShape(18.dp)
    Button(
        onClick = onStart,
        shape = shape,
        interactionSource = interactionSource,
        contentPadding = PaddingValues(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = OnAccent
        ),
        modifier = modifier
            .scale(scale)
            .shadow(elevation = 16.dp, shape = shape, ambientColor = Accent, spotColor = Accent)
            .background(Brush.linearGradient(listOf(Accent, AccentDeep)), shape)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(64.dp)
            )
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = stringResource(R.string.menu_cta_hint),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp),
                    color = AccentSoft,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = OnAccent,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.cta_start),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp,
                            lineHeight = 24.sp
                        ),
                        color = OnAccent,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun StatColumn(state: MenuUiState, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        if (state.hasStats) {
            StatCard(
                value = durationText(state.focusedMinutes),
                label = stringResource(R.string.stat_focused_today),
                accent = CatStudy,
                live = true,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(
                value = stringResource(R.string.fmt_days, state.streak),
                label = stringResource(R.string.stat_day_streak),
                accent = Accent,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
        } else {
            BentoTile(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.menu_no_focus),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoriesTile(count: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    BentoTile(modifier = modifier, onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            tint = PrimaryBright,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.menu_categories),
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
            color = TextPrimary,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(R.string.menu_categories_count, count),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = TextMuted,
            maxLines = 1
        )
    }
}

@Composable
private fun WeeklyTile(
    weekMinutes: List<Int>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val peak = (weekMinutes.maxOrNull() ?: 0).coerceAtLeast(1)
    BentoTile(modifier = modifier, onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = CatHealth,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                weekMinutes.forEachIndexed { index, minutes ->
                    val height = (8 + (minutes * 20 / peak)).coerceIn(6, 28)
                    Spacer(
                        modifier = Modifier
                            .width(5.dp)
                            .height(height.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(barColor(index))
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.menu_weekly),
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
            color = TextPrimary,
            maxLines = 1
        )
    }
}

private fun barColor(index: Int): Color = when (index % 4) {
    0 -> PrimaryBright
    1 -> CatStudy
    2 -> Accent
    else -> CatHealth
}
