package com.focusblock.planner.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.focusblock.planner.core.theme.BorderHair
import com.focusblock.planner.core.theme.SurfaceCard
import com.focusblock.planner.core.theme.TextMuted
import com.focusblock.planner.core.theme.TextPrimary

@Composable
fun SectionHeader(
    eyebrow: String,
    title: String,
    modifier: Modifier = Modifier,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (leading != null) leading()
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = eyebrow,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                maxLines = 1
            )
        }
        if (trailing != null) trailing()
    }
}

@Composable
fun IconTile(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(shape)
            .background(SurfaceCard)
            .border(1.dp, BorderHair, shape)
            .clickable(enabled = enabled, onClickLabel = contentDescription, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
