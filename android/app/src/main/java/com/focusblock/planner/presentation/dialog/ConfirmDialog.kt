package com.focusblock.planner.presentation.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.focusblock.planner.core.theme.Accent
import com.focusblock.planner.core.theme.SurfaceCard
import com.focusblock.planner.core.theme.TextPrimary
import com.focusblock.planner.core.theme.TextSecondary
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(20.dp),
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        },
        text = {
            Text(text = message, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Accent
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    )
}
