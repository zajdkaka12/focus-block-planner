package com.focusblock.planner.presentation.blockeditor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusblock.planner.R
import com.focusblock.planner.core.config.GameConfig
import com.focusblock.planner.core.di.AppViewModelFactory
import com.focusblock.planner.core.theme.Accent
import com.focusblock.planner.core.theme.BorderHair
import com.focusblock.planner.core.theme.CatHealth
import com.focusblock.planner.core.theme.DividerSoft
import com.focusblock.planner.core.theme.SurfaceCard
import com.focusblock.planner.core.theme.TextMuted
import com.focusblock.planner.core.theme.TextPrimary
import com.focusblock.planner.core.theme.TextSecondary
import com.focusblock.planner.core.ui.IconTile
import com.focusblock.planner.core.ui.PrimaryButton
import com.focusblock.planner.core.ui.ScreenScaffold
import com.focusblock.planner.core.ui.SectionHeader
import com.focusblock.planner.core.ui.StepperRow
import com.focusblock.planner.core.ui.argbColor
import com.focusblock.planner.core.ui.categorySprite
import com.focusblock.planner.core.ui.timeText

private val EditorOverlay = listOf(
    Color(0xF5141022),
    Color(0xE61B1534),
    Color(0xF5141022)
)

@Composable
fun BlockEditorRoute(
    blockId: Long,
    startMinutes: Int,
    onClose: () -> Unit
) {
    val editorViewModel: BlockEditorViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by editorViewModel.uiState.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(blockId, startMinutes) {
        editorViewModel.load(blockId, startMinutes)
    }
    LaunchedEffect(Unit) {
        editorViewModel.closeEvents.collect { onClose() }
    }
    LaunchedEffect(state.confirmTick) {
        if (state.confirmTick > 0 && state.hapticsEnabled) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
    BlockEditorScreen(
        state = state,
        onClose = onClose,
        onTitleChange = editorViewModel::onTitleChange,
        onCategoryChange = editorViewModel::onCategoryChange,
        onLengthChange = editorViewModel::changeLength,
        onStartChange = editorViewModel::changeStart,
        onSave = editorViewModel::save,
        onDelete = editorViewModel::delete
    )
}

@Composable
private fun BlockEditorScreen(
    state: BlockEditorUiState,
    onClose: () -> Unit,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onLengthChange: (Int) -> Unit,
    onStartChange: (Int) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    ScreenScaffold(overlay = EditorOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            SectionHeader(
                eyebrow = stringResource(R.string.editor_eyebrow),
                title = stringResource(
                    if (state.editing) R.string.editor_title_edit else R.string.editor_title_new
                ),
                modifier = Modifier.fillMaxWidth(),
                trailing = {
                    IconTile(
                        contentDescription = stringResource(R.string.cd_close),
                        onClick = onClose
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(18.dp))
            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                label = {
                    Text(
                        text = stringResource(R.string.editor_task_label),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                isError = state.showTitleError,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = DividerSoft,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = Accent,
                    focusedLabelColor = Accent,
                    unfocusedLabelColor = TextMuted
                ),
                modifier = Modifier.fillMaxWidth()
            )
            AnimatedVisibility(
                visible = state.showTitleError,
                enter = fadeIn(tween(160)),
                exit = fadeOut(tween(160))
            ) {
                Text(
                    text = stringResource(R.string.editor_needs_title),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Accent,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.editor_category),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(10.dp))
            CategoryGrid(
                state = state,
                onCategoryChange = onCategoryChange
            )
            Spacer(modifier = Modifier.height(16.dp))
            StepperRow(
                label = stringResource(R.string.editor_length),
                value = stringResource(R.string.editor_minutes, state.lengthMinutes),
                minusDescription = stringResource(R.string.cd_minus),
                plusDescription = stringResource(R.string.cd_plus),
                onMinus = { onLengthChange(-GameConfig.BLOCK_STEP_MINUTES) },
                onPlus = { onLengthChange(GameConfig.BLOCK_STEP_MINUTES) },
                minusEnabled = state.canShrink,
                plusEnabled = state.canGrow
            )
            Spacer(modifier = Modifier.height(12.dp))
            StepperRow(
                label = stringResource(R.string.editor_start),
                value = timeText(state.startMinutes),
                minusDescription = stringResource(R.string.cd_minus),
                plusDescription = stringResource(R.string.cd_plus),
                onMinus = { onStartChange(-GameConfig.SLOT_MINUTES) },
                onPlus = { onStartChange(GameConfig.SLOT_MINUTES) },
                minusEnabled = state.canMoveEarlier,
                plusEnabled = state.canMoveLater
            )
            AnimatedVisibility(
                visible = state.showFitError,
                enter = fadeIn(tween(160)),
                exit = fadeOut(tween(160))
            ) {
                Text(
                    text = stringResource(R.string.game_error_fit),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Accent,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PrimaryButton(
                    text = stringResource(R.string.cta_save_block),
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth()
                )
                SaveConfirmCheck(visible = state.confirmTick > 0)
            }
            if (state.editing) {
                Spacer(modifier = Modifier.height(6.dp))
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text(
                        text = stringResource(R.string.cta_delete_block),
                        style = MaterialTheme.typography.labelSmall,
                        color = Accent
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun SaveConfirmCheck(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(spring(dampingRatio = 0.6f)) + fadeIn(tween(180)),
        exit = fadeOut(tween(120))
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = CatHealth,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun CategoryGrid(
    state: BlockEditorUiState,
    onCategoryChange: (String) -> Unit
) {
    val rows = state.categories.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { category ->
                    val color = argbColor(category.colorArgb)
                    val selected = category.id == state.categoryId
                    val shape = RoundedCornerShape(16.dp)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp)
                            .clip(shape)
                            .background(if (selected) color.copy(alpha = 0.12f) else SurfaceCard)
                            .border(
                                width = if (selected) 1.5.dp else 1.dp,
                                color = if (selected) color else BorderHair,
                                shape = shape
                            )
                            .clickable { onCategoryChange(category.id) }
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(categorySprite(category.spriteIndex)),
                            contentDescription = stringResource(
                                R.string.cd_category,
                                category.name
                            ),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = category.name.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = if (selected) color else TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start
                        )
                    }
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
