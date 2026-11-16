package com.focusblock.planner.core.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusblock.planner.core.theme.Accent
import com.focusblock.planner.core.theme.BorderSoft
import com.focusblock.planner.core.theme.CatHealth
import com.focusblock.planner.core.theme.TextPrimary
import kotlin.math.roundToInt

@Composable
fun FocusBlockCard(
    blockKey: Long,
    title: String,
    subtitle: String,
    stateText: String,
    color: Color,
    selected: Boolean,
    isActive: Boolean,
    isDone: Boolean,
    progress: Float,
    slotHeightPx: Float,
    onTap: () -> Unit,
    onMove: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var dragOffset by remember(blockKey) { mutableFloatStateOf(0f) }
    var dragging by remember(blockKey) { mutableStateOf(false) }
    val pulse = rememberInfiniteTransition(label = "blockPulse")
    val pulseAlpha by pulse.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blockPulseAlpha"
    )
    val lift by animateFloatAsState(
        targetValue = if (dragging) 1.03f else 1f,
        animationSpec = tween(120),
        label = "blockLift"
    )
    val fillProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1000),
        label = "blockFill"
    )
    val shape = RoundedCornerShape(16.dp)
    val borderColor = when {
        isActive -> Accent.copy(alpha = pulseAlpha)
        isDone -> CatHealth.copy(alpha = 0.7f)
        selected -> color
        else -> BorderSoft
    }
    Box(
        modifier = modifier
            .offset { IntOffset(0, dragOffset.roundToInt()) }
            .scale(lift)
            .shadow(if (dragging) 14.dp else 4.dp, shape, ambientColor = color, spotColor = color)
            .clip(shape)
            .alpha(if (isDone) 0.55f else 1f)
            .background(Brush.horizontalGradient(listOf(color, darken(color))))
            .border(if (selected || isActive) 1.5.dp else 1.dp, borderColor, shape)
            .semantics { stateDescription = stateText }
            .clickable(onClick = onTap)
            .pointerInput(blockKey, slotHeightPx, isDone) {
                if (!isDone) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { dragging = true },
                        onDrag = { change, amount ->
                            change.consume()
                            dragOffset += amount.y
                        },
                        onDragEnd = {
                            val slots = if (slotHeightPx > 0f) {
                                (dragOffset / slotHeightPx).roundToInt()
                            } else {
                                0
                            }
                            dragOffset = 0f
                            dragging = false
                            onMove(slots)
                        },
                        onDragCancel = {
                            dragOffset = 0f
                            dragging = false
                        }
                    )
                }
            }
    ) {
        if (isActive && fillProgress > 0f) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .fillMaxHeight(fillProgress)
                    .background(Accent.copy(alpha = 0.33f))
            )
        }
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(color)
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 14.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.size(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp
                    ),
                    color = Color(0xFFD7D2E4),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = CatHealth,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
