package com.focusblock.planner.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.focusblock.planner.core.theme.DividerSoft

data class BarSegment(val color: Color, val minutes: Int)

@Composable
fun CategoryBar(
    segments: List<BarSegment>,
    modifier: Modifier = Modifier,
    barHeight: Dp = 14.dp
) {
    val visible = segments.filter { it.minutes > 0 }
    val total = visible.sumOf { it.minutes }
    val grown by animateFloatAsState(
        targetValue = if (total > 0) 1f else 0f,
        animationSpec = tween(420),
        label = "categoryBar"
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .clip(RoundedCornerShape(barHeight / 2))
            .background(DividerSoft)
    ) {
        if (total > 0) {
            visible.forEach { segment ->
                Spacer(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(segment.minutes.toFloat() * grown + 0.0001f)
                        .background(segment.color)
                )
            }
            if (grown < 1f) {
                Spacer(modifier = Modifier.weight(total.toFloat() * (1f - grown) + 0.0001f))
            }
        }
    }
}
