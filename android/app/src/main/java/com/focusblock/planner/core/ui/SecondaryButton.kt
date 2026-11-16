package com.focusblock.planner.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusblock.planner.core.theme.DividerSoft
import com.focusblock.planner.core.theme.TextFaint
import com.focusblock.planner.core.theme.TextSecondary

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentColor: Color = TextSecondary,
    leading: @Composable (() -> Unit)? = null
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, DividerSoft),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor,
            disabledContentColor = TextFaint
        ),
        modifier = modifier.height(48.dp)
    ) {
        if (leading != null) {
            leading()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp
            ),
            maxLines = 1
        )
    }
}
