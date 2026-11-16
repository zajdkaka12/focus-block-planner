package com.focusblock.planner.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusblock.planner.core.theme.BorderHair
import com.focusblock.planner.core.theme.DividerSoft
import com.focusblock.planner.core.theme.TextMuted
import com.focusblock.planner.core.theme.TextPrimary

@Composable
fun StepperRow(
    label: String,
    value: String,
    minusDescription: String,
    plusDescription: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier,
    minusEnabled: Boolean = true,
    plusEnabled: Boolean = true
) {
    BentoTile(modifier = modifier.fillMaxWidth(), contentPadding = 12.dp) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StepperTile(
                description = minusDescription,
                enabled = minusEnabled,
                onClick = onMinus
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 16.dp, height = 2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(TextPrimary)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                ),
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .semantics { stateDescription = value }
            )
            StepperTile(
                description = plusDescription,
                enabled = plusEnabled,
                onClick = onPlus
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun StepperTile(
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .size(48.dp)
            .alpha(if (enabled) 1f else 0.35f)
            .clip(shape)
            .background(DividerSoft)
            .border(1.dp, BorderHair, shape)
            .clickable(enabled = enabled, onClickLabel = description, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
