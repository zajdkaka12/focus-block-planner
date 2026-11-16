package com.focusblock.planner.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusblock.planner.core.theme.Accent
import com.focusblock.planner.core.theme.AccentDeep
import com.focusblock.planner.core.theme.DividerSoft
import com.focusblock.planner.core.theme.OnAccent

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    height: Dp = 58.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 900f),
        label = "primaryPress"
    )
    val shape = RoundedCornerShape(16.dp)
    val fill = if (enabled) {
        Brush.linearGradient(listOf(Accent, AccentDeep))
    } else {
        Brush.linearGradient(listOf(DividerSoft, DividerSoft))
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = OnAccent,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = OnAccent
        ),
        modifier = modifier
            .height(height)
            .scale(scale)
            .shadow(
                elevation = if (enabled) 14.dp else 0.dp,
                shape = shape,
                ambientColor = Accent,
                spotColor = Accent
            )
            .background(fill, shape)
            .alpha(if (enabled) 1f else 0.4f)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp,
                lineHeight = 24.sp
            ),
            maxLines = 1
        )
    }
}
