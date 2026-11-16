package com.focusblock.planner.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.focusblock.planner.core.theme.TextSecondary

@Composable
fun EmptyStateCard(
    message: String,
    modifier: Modifier = Modifier,
    spriteRes: Int? = null,
    spriteDescription: String? = null,
    spriteSize: Dp = 96.dp
) {
    BentoTile(modifier = modifier.fillMaxWidth(), contentPadding = 20.dp) {
        if (spriteRes != null) {
            Image(
                painter = painterResource(spriteRes),
                contentDescription = spriteDescription,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(spriteSize)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
        Text(
            text = message,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
