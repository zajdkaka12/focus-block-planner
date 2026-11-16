package com.focusblock.planner.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusblock.planner.R
import com.focusblock.planner.core.theme.BorderHair
import com.focusblock.planner.core.theme.SurfaceCard
import com.focusblock.planner.core.theme.TextSecondary

@Composable
fun CategoryChip(
    name: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    val stateText = stringResource(
        if (selected) R.string.sd_selected else R.string.sd_not_selected
    )
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(shape)
            .background(if (selected) color.copy(alpha = 0.12f) else SurfaceCard)
            .border(
                width = if (selected) 1.5.dp else 1.dp,
                color = if (selected) color else BorderHair,
                shape = shape
            )
            .clickable(onClick = onClick)
            .semantics { stateDescription = stateText }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 1.2.sp
            ),
            color = if (selected) color else TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
