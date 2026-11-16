package com.focusblock.planner.presentation.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusblock.planner.R
import com.focusblock.planner.core.config.GameConfig
import com.focusblock.planner.core.di.AppViewModelFactory
import com.focusblock.planner.core.theme.Accent
import com.focusblock.planner.core.theme.BgRaised
import com.focusblock.planner.core.theme.BorderHair
import com.focusblock.planner.core.theme.CatHealth
import com.focusblock.planner.core.theme.CatStudy
import com.focusblock.planner.core.theme.DividerSoft
import com.focusblock.planner.core.theme.PrimaryBright
import com.focusblock.planner.core.theme.SurfaceCard
import com.focusblock.planner.core.theme.TextFaint
import com.focusblock.planner.core.theme.TextMuted
import com.focusblock.planner.core.theme.TextPrimary
import com.focusblock.planner.core.theme.TextSecondary
import com.focusblock.planner.core.ui.CategoryChip
import com.focusblock.planner.core.ui.EmptyStateCard
import com.focusblock.planner.core.ui.FocusBlockCard
import com.focusblock.planner.core.ui.IconTile
import com.focusblock.planner.core.ui.PrimaryButton
import com.focusblock.planner.core.ui.ScreenScaffold
import com.focusblock.planner.core.ui.SecondaryButton
import com.focusblock.planner.core.ui.StatCard
import com.focusblock.planner.core.ui.argbColor
import com.focusblock.planner.core.ui.countText
import com.focusblock.planner.core.ui.countdownText
import com.focusblock.planner.core.ui.durationText
import com.focusblock.planner.core.ui.timeRangeText
import com.focusblock.planner.core.ui.timeText
import com.focusblock.planner.domain.model.DaySummary

private val GameOverlay = listOf(
    Color(0xF0141022),
    Color(0xCC1A1333),
    Color(0xF5141022)
)

private val RailWidth = 56.dp

@Composable
fun GameRoute(
    onBack: () -> Unit,
    onEditBlock: (Long, Int) -> Unit,
    onDayComplete: (DaySummary) -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by gameViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.OpenEditor -> onEditBlock(event.blockId, event.startMinutes)
                is GameEvent.DayComplete -> onDayComplete(event.summary)
            }
        }
    }
    DisposableEffect(Unit) {
        gameViewModel.refresh()
        onDispose { gameViewModel.pauseTimer() }
    }
    GameScreen(
        state = state,
        onBack = onBack,
        onTogglePause = gameViewModel::togglePause,
        onSelectBlock = gameViewModel::selectBlock,
        onEditSelected = gameViewModel::requestEditSelected,
        onNewBlock = gameViewModel::requestNewBlock,
        onMoveBlock = gameViewModel::moveBlockBySlots,
        onSelectCategory = gameViewModel::selectCategory,
        onStartBlock = gameViewModel::startSelectedBlock,
        onCompleteBlock = gameViewModel::completeActiveBlock,
        onCancelBlock = gameViewModel::cancelActiveBlock,
        onFinishDay = gameViewModel::finishDay
    )
}

@Composable
private fun GameScreen(
    state: GameUiState,
    onBack: () -> Unit,
    onTogglePause: () -> Unit,
    onSelectBlock: (Long) -> Unit,
    onEditSelected: () -> Unit,
    onNewBlock: (Int) -> Unit,
    onMoveBlock: (Long, Int) -> Unit,
    onSelectCategory: (String) -> Unit,
    onStartBlock: () -> Unit,
    onCompleteBlock: () -> Unit,
    onCancelBlock: () -> Unit,
    onFinishDay: () -> Unit
) {
    ScreenScaffold(overlay = GameOverlay) {
        GameHeader(
            state = state,
            onBack = onBack,
            onTogglePause = onTogglePause
        )
        Timeline(
            state = state,
            onSelectBlock = onSelectBlock,
            onNewBlock = onNewBlock,
            onMoveBlock = onMoveBlock,
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
        SplitPanel(
            state = state,
            onSelectCategory = onSelectCategory,
            onEditSelected = onEditSelected,
            onStartBlock = onStartBlock,
            onCompleteBlock = onCompleteBlock,
            onCancelBlock = onCancelBlock,
            onFinishDay = onFinishDay
        )
    }
}

@Composable
private fun GameHeader(
    state: GameUiState,
    onBack: () -> Unit,
    onTogglePause: () -> Unit
) {
    val pauseEnabled = state.mode == GameMode.Running
    Surface(
        color = SurfaceCard.copy(alpha = 0.5f),
        shape = RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.game_eyebrow),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = timeRangeText(state.dayStartMinutes, state.dayEndMinutes),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    maxLines = 1
                )
            }
            IconTile(
                contentDescription = stringResource(R.string.cd_pause),
                onClick = onTogglePause,
                enabled = pauseEnabled,
                modifier = Modifier.alpha(if (pauseEnabled) 1f else 0.35f)
            ) {
                if (state.paused) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.icon_pause),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Timeline(
    state: GameUiState,
    onSelectBlock: (Long) -> Unit,
    onNewBlock: (Int) -> Unit,
    onMoveBlock: (Long, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.padding(horizontal = 12.dp)) {
        val slotHeight = maxHeight / GameConfig.VISIBLE_SLOTS
        val contentHeight = slotHeight * state.totalSlots
        val slotHeightPx = with(LocalDensity.current) { slotHeight.toPx() }
        val scrollState = rememberScrollState()
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
            Row(modifier = Modifier.fillMaxWidth().height(contentHeight)) {
                HourRail(
                    state = state,
                    slotHeight = slotHeight,
                    modifier = Modifier.width(RailWidth).fillMaxHeight()
                )
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        repeat(state.totalSlots) { index ->
                            val minute = state.dayStartMinutes + index * GameConfig.SLOT_MINUTES
                            val label = stringResource(R.string.cd_add_block, timeText(minute))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(slotHeight)
                                    .clickable(onClickLabel = label) { onNewBlock(minute) }
                            ) {
                                Spacer(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color(0xFF241C3F))
                                )
                            }
                        }
                    }
                    state.blocks.forEach { block ->
                        val category = state.categoryFor(block.categoryId)
                        val color = category?.let { argbColor(it.colorArgb) } ?: PrimaryBright
                        val top = slotHeight *
                            ((block.startMinutes - state.dayStartMinutes).toFloat() /
                                GameConfig.SLOT_MINUTES)
                        val rawHeight = slotHeight *
                            (block.lengthMinutes.toFloat() / GameConfig.SLOT_MINUTES)
                        val blockHeight = (rawHeight - 6.dp).coerceAtLeast(46.dp)
                        val stateText = stringResource(
                            when {
                                block.done -> R.string.sd_block_done
                                block.id == state.activeBlockId -> R.string.sd_block_running
                                else -> R.string.sd_block_planned
                            }
                        )
                        FocusBlockCard(
                            blockKey = block.id,
                            title = block.title,
                            subtitle = stringResource(
                                R.string.fmt_dot_pair,
                                timeRangeText(block.startMinutes, block.endMinutes),
                                category?.name.orEmpty()
                            ),
                            stateText = stateText,
                            color = color,
                            selected = block.id == state.selectedBlockId,
                            isActive = block.id == state.activeBlockId,
                            isDone = block.done,
                            progress = if (block.id == state.activeBlockId) state.progress else 0f,
                            slotHeightPx = slotHeightPx,
                            onTap = { onSelectBlock(block.id) },
                            onMove = { slots -> onMoveBlock(block.id, slots) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = top)
                                .height(blockHeight)
                        )
                    }
                }
            }
        }
        if (state.blocks.isEmpty() && !state.loading) {
            EmptyStateCard(
                message = stringResource(R.string.game_empty),
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun HourRail(
    state: GameUiState,
    slotHeight: Dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(1.dp)
                .fillMaxHeight()
                .background(DividerSoft)
        )
        Column(modifier = Modifier.fillMaxSize()) {
            repeat(state.totalSlots) { index ->
                val minute = state.dayStartMinutes + index * GameConfig.SLOT_MINUTES
                Box(modifier = Modifier.fillMaxWidth().height(slotHeight)) {
                    if (minute % 120 == 0) {
                        Text(
                            text = stringResource(R.string.fmt_hour_label, minute / 60),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextFaint,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(end = 10.dp)
                        )
                    }
                }
            }
        }
        if (state.nowMinutes in state.dayStartMinutes until state.dayEndMinutes) {
            val nowOffset = slotHeight *
                ((state.nowMinutes - state.dayStartMinutes).toFloat() / GameConfig.SLOT_MINUTES)
            Row(
                modifier = Modifier
                    .offset(y = nowOffset)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(Accent)
                )
                Spacer(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Accent)
                )
            }
        }
    }
}

@Composable
private fun SplitPanel(
    state: GameUiState,
    onSelectCategory: (String) -> Unit,
    onEditSelected: () -> Unit,
    onStartBlock: () -> Unit,
    onCompleteBlock: () -> Unit,
    onCancelBlock: () -> Unit,
    onFinishDay: () -> Unit
) {
    Surface(
        color = BgRaised.copy(alpha = 0.95f),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = BorderHair,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )
                .padding(16.dp)
        ) {
            AnimatedVisibility(
                visible = state.errorVisible,
                enter = fadeIn(tween(160)),
                exit = fadeOut(tween(160))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Accent.copy(alpha = 0.16f))
                        .border(1.dp, Accent, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.game_error_fit),
                        style = MaterialTheme.typography.labelSmall,
                        color = Accent,
                        textAlign = TextAlign.Center
                    )
                }
            }
            StatStrip(state = state)
            Spacer(modifier = Modifier.height(12.dp))
            AnimatedContent(
                targetState = state.mode,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(160)) },
                label = "panelMode"
            ) { mode ->
                if (mode == GameMode.Planning) {
                    PlanningControls(
                        state = state,
                        onSelectCategory = onSelectCategory,
                        onEditSelected = onEditSelected,
                        onStartBlock = onStartBlock,
                        onFinishDay = onFinishDay
                    )
                } else {
                    RunningControls(
                        state = state,
                        onCompleteBlock = onCompleteBlock,
                        onCancelBlock = onCancelBlock
                    )
                }
            }
        }
    }
}

@Composable
private fun StatStrip(state: GameUiState) {
    val showPlanned = state.plannedMinutes > 0
    val showDone = state.blocksDone > 0
    val showFocused = state.focusedMinutes > 0
    val visible = listOf(showPlanned, showDone, showFocused).count { it }
    if (visible < 2) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (showPlanned) {
            StatCard(
                value = durationText(state.plannedMinutes),
                label = stringResource(R.string.stat_planned),
                accent = PrimaryBright,
                live = true,
                modifier = Modifier.weight(1f)
            )
        }
        if (showDone) {
            StatCard(
                value = countText(state.blocksDone, state.blocks.size),
                label = stringResource(R.string.stat_done),
                accent = CatHealth,
                live = true,
                modifier = Modifier.weight(1f)
            )
        }
        if (showFocused) {
            StatCard(
                value = durationText(state.focusedMinutes),
                label = stringResource(R.string.stat_focused),
                accent = CatStudy,
                live = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PlanningControls(
    state: GameUiState,
    onSelectCategory: (String) -> Unit,
    onEditSelected: () -> Unit,
    onStartBlock: () -> Unit,
    onFinishDay: () -> Unit
) {
    val selectedBlock = remember(state.selectedBlockId, state.blocks) {
        state.blocks.firstOrNull { it.id == state.selectedBlockId }
    }
    val canStart = selectedBlock != null && !selectedBlock.done
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.categories.take(4).forEach { category ->
                CategoryChip(
                    name = category.name.uppercase(),
                    color = argbColor(category.colorArgb),
                    selected = category.id == state.selectedCategoryId,
                    onClick = { onSelectCategory(category.id) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryButton(
            text = stringResource(R.string.cta_start_block),
            onClick = onStartBlock,
            enabled = canStart,
            icon = Icons.Default.PlayArrow,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecondaryButton(
                text = stringResource(R.string.cta_finish_day),
                onClick = onFinishDay,
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                text = stringResource(R.string.editor_title_edit),
                onClick = onEditSelected,
                enabled = selectedBlock != null,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RunningControls(
    state: GameUiState,
    onCompleteBlock: () -> Unit,
    onCancelBlock: () -> Unit
) {
    val minutesLeft = (state.remainingSeconds + 59) / 60
    val announcement = stringResource(R.string.sd_minutes_left, minutesLeft)
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = state.activeBlock?.title.orEmpty(),
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = countdownText(state.remainingSeconds),
            style = MaterialTheme.typography.displaySmall.copy(fontFeatureSettings = "tnum"),
            color = if (state.paused) TextSecondary else TextPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    liveRegion = LiveRegionMode.Polite
                    stateDescription = announcement
                }
        )
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { state.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = Accent,
            trackColor = DividerSoft
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(
                if (state.paused) R.string.game_paused else R.string.game_running_now
            ),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = if (state.paused) TextMuted else Accent
        )
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryButton(
            text = stringResource(R.string.cta_complete_block),
            onClick = onCompleteBlock,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        SecondaryButton(
            text = stringResource(R.string.cta_cancel_block),
            onClick = onCancelBlock,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
