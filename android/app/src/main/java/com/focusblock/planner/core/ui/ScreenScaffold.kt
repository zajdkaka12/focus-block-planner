package com.focusblock.planner.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.focusblock.planner.R
import com.focusblock.planner.core.theme.BgDeep

@Composable
fun ScreenScaffold(
    overlay: List<Color>,
    modifier: Modifier = Modifier,
    decor: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(BgDeep)) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(overlay)))
        decor()
        Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding(),
            content = content
        )
    }
}
