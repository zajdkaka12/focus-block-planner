package com.focusblock.planner.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusblock.planner.core.theme.TextMuted

@Composable
fun StatCard(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier,
    live: Boolean = false
) {
    BentoTile(modifier = modifier, contentPadding = 14.dp) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(accent)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            ),
            color = accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = if (live) {
                Modifier
                    .fillMaxWidth()
                    .semantics { liveRegion = LiveRegionMode.Polite }
            } else {
                Modifier.fillMaxWidth()
            }
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.4.sp),
            color = TextMuted,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
