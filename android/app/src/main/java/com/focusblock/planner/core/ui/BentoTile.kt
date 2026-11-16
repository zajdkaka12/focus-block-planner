package com.focusblock.planner.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.focusblock.planner.core.theme.BorderHair
import com.focusblock.planner.core.theme.SurfaceCard

@Composable
fun BentoTile(
    modifier: Modifier = Modifier,
    containerColor: Color = SurfaceCard,
    borderColor: Color = BorderHair,
    borderWidth: Dp = 1.dp,
    corner: Dp = 18.dp,
    elevation: Dp = 6.dp,
    contentPadding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(corner)
    val base = modifier
        .shadow(elevation, shape)
        .clip(shape)
        .background(containerColor)
        .border(borderWidth, borderColor, shape)
    val resolved = if (onClick == null) base else base.clickable(onClick = onClick)
    Column(modifier = resolved.padding(contentPadding), content = content)
}
