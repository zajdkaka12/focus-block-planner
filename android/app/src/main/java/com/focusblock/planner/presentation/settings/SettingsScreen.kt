package com.focusblock.planner.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusblock.planner.R
import com.focusblock.planner.core.config.GameConfig
import com.focusblock.planner.core.di.AppViewModelFactory
import com.focusblock.planner.core.theme.Accent
import com.focusblock.planner.core.theme.DividerSoft
import com.focusblock.planner.core.theme.OnAccent
import com.focusblock.planner.core.theme.SurfaceCard
import com.focusblock.planner.core.theme.TextMuted
import com.focusblock.planner.core.theme.TextPrimary
import com.focusblock.planner.core.theme.TextSecondary
import com.focusblock.planner.core.ui.BentoTile
import com.focusblock.planner.core.ui.IconTile
import com.focusblock.planner.core.ui.ScreenScaffold
import com.focusblock.planner.core.ui.SectionHeader
import com.focusblock.planner.core.ui.SecondaryButton
import com.focusblock.planner.core.ui.StepperRow
import com.focusblock.planner.core.ui.timeText
import com.focusblock.planner.presentation.dialog.ConfirmDialog

private val SettingsOverlay = listOf(
    Color(0xE6141022),
    Color(0xEB1B1533),
    Color(0xF5141022)
)

@Composable
fun SettingsRoute(onBack: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory.factory)
    val state by settingsViewModel.uiState.collectAsStateWithLifecycle()
    DisposableEffect(Unit) {
        settingsViewModel.refresh()
        onDispose { }
    }
    SettingsScreen(
        state = state,
        onBack = onBack,
        onDayStartChange = settingsViewModel::changeDayStart,
        onDayEndChange = settingsViewModel::changeDayEnd,
        onDefaultBlockChange = settingsViewModel::changeDefaultBlock,
        onAutoStartChange = settingsViewModel::setAutoStart,
        onHapticsChange = settingsViewModel::setHaptics,
        onRequestReset = settingsViewModel::requestReset,
        onConfirmReset = settingsViewModel::confirmReset,
        onDismissReset = settingsViewModel::dismissReset
    )
}

@Composable
private fun SettingsScreen(
    state: SettingsUiState,
    onBack: () -> Unit,
    onDayStartChange: (Int) -> Unit,
    onDayEndChange: (Int) -> Unit,
    onDefaultBlockChange: (Int) -> Unit,
    onAutoStartChange: (Boolean) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onRequestReset: () -> Unit,
    onConfirmReset: () -> Unit,
    onDismissReset: () -> Unit
) {
    ScreenScaffold(overlay = SettingsOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            SectionHeader(
                eyebrow = stringResource(R.string.settings_eyebrow),
                title = stringResource(R.string.settings_title),
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
            StepperRow(
                label = stringResource(R.string.settings_day_start),
                value = timeText(state.settings.dayStartMinutes),
                minusDescription = stringResource(R.string.cd_minus),
                plusDescription = stringResource(R.string.cd_plus),
                onMinus = { onDayStartChange(-GameConfig.SLOT_MINUTES) },
                onPlus = { onDayStartChange(GameConfig.SLOT_MINUTES) },
                minusEnabled = state.canStartEarlier,
                plusEnabled = state.canStartLater
            )
            Spacer(modifier = Modifier.height(12.dp))
            StepperRow(
                label = stringResource(R.string.settings_day_end),
                value = timeText(state.settings.dayEndMinutes),
                minusDescription = stringResource(R.string.cd_minus),
                plusDescription = stringResource(R.string.cd_plus),
                onMinus = { onDayEndChange(-GameConfig.SLOT_MINUTES) },
                onPlus = { onDayEndChange(GameConfig.SLOT_MINUTES) },
                minusEnabled = state.canEndEarlier,
                plusEnabled = state.canEndLater
            )
            Spacer(modifier = Modifier.height(12.dp))
            StepperRow(
                label = stringResource(R.string.settings_default_length),
                value = stringResource(
                    R.string.editor_minutes,
                    state.settings.defaultBlockMinutes
                ),
                minusDescription = stringResource(R.string.cd_minus),
                plusDescription = stringResource(R.string.cd_plus),
                onMinus = { onDefaultBlockChange(-GameConfig.BLOCK_STEP_MINUTES) },
                onPlus = { onDefaultBlockChange(GameConfig.BLOCK_STEP_MINUTES) },
                minusEnabled = state.canShrinkBlock,
                plusEnabled = state.canGrowBlock
            )
            Spacer(modifier = Modifier.height(12.dp))
            SwitchRow(
                title = stringResource(R.string.settings_auto_start),
                hint = stringResource(R.string.settings_auto_start_hint),
                checked = state.settings.autoStartNext,
                onCheckedChange = onAutoStartChange
            )
            Spacer(modifier = Modifier.height(12.dp))
            SwitchRow(
                title = stringResource(R.string.settings_haptics),
                hint = stringResource(R.string.settings_haptics_hint),
                checked = state.settings.hapticsEnabled,
                onCheckedChange = onHapticsChange
            )
            Spacer(modifier = Modifier.height(24.dp))
            SecondaryButton(
                text = stringResource(R.string.cta_reset_day),
                onClick = onRequestReset,
                contentColor = Accent,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    if (state.confirmReset) {
        ConfirmDialog(
            title = stringResource(R.string.confirm_reset_title),
            message = stringResource(R.string.confirm_reset_message),
            confirmLabel = stringResource(R.string.confirm_reset),
            dismissLabel = stringResource(R.string.confirm_cancel),
            onConfirm = onConfirmReset,
            onDismiss = onDismissReset
        )
    }
}

@Composable
private fun SwitchRow(
    title: String,
    hint: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val stateText = stringResource(
        if (checked) R.string.sd_selected else R.string.sd_not_selected
    )
    BentoTile(modifier = Modifier.fillMaxWidth(), contentPadding = 14.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { stateDescription = stateText },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextMuted,
                    maxLines = 2
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = OnAccent,
                    checkedTrackColor = Accent,
                    checkedBorderColor = Accent,
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = SurfaceCard,
                    uncheckedBorderColor = DividerSoft
                )
            )
        }
    }
}
