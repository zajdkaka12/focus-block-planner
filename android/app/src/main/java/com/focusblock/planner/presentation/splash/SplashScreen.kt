package com.focusblock.planner.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusblock.planner.R
import com.focusblock.planner.core.di.AppViewModelFactory
import com.focusblock.planner.core.theme.Accent
import com.focusblock.planner.core.theme.BorderHair
import com.focusblock.planner.core.theme.CatStudy
import com.focusblock.planner.core.theme.DividerSoft
import com.focusblock.planner.core.theme.Primary
import com.focusblock.planner.core.theme.TextMuted
import com.focusblock.planner.core.theme.TextPrimary
import com.focusblock.planner.core.ui.ScreenScaffold

private val SplashOverlay = listOf(
    Color(0xF0141022),
    Color(0xFA141022),
    Color(0xFF0E0A19)
)

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = AppViewModelFactory.factory)
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()
    LaunchedEffect(finished) {
        if (finished) onFinished()
    }
    SplashScreen()
}

@Composable
private fun SplashScreen() {
    val transition = rememberInfiniteTransition(label = "splash")
    val barOne by transition.animateFloat(
        initialValue = -14f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "barOne"
    )
    val barTwo by transition.animateFloat(
        initialValue = 12f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "barTwo"
    )
    val breathe by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )
    val glow by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.34f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val cardState = remember { MutableTransitionState(false).apply { targetState = true } }
    val titleState = remember { MutableTransitionState(false).apply { targetState = true } }
    val taglineState = remember { MutableTransitionState(false).apply { targetState = true } }

    ScreenScaffold(overlay = SplashOverlay) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visibleState = cardState,
                enter = fadeIn(tween(420)) + slideInVertically { it / 12 }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .alpha(glow)
                            .background(
                                Brush.radialGradient(
                                    listOf(Primary, Color.Transparent)
                                )
                            )
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SplashBar(width = 120, color = Primary, shift = barOne)
                        SplashBar(width = 160, color = Accent, shift = barTwo)
                        SplashBar(width = 96, color = CatStudy, shift = barOne)
                    }
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(breathe)
                            .shadow(
                                elevation = 18.dp,
                                shape = RoundedCornerShape(36.dp),
                                ambientColor = Primary,
                                spotColor = Primary
                            )
                            .clip(RoundedCornerShape(36.dp))
                            .background(
                                Brush.linearGradient(listOf(Primary, Color(0xFF2A1A63)))
                            )
                            .border(1.dp, BorderHair, RoundedCornerShape(36.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = stringResource(R.string.cd_logo),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(128.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            AnimatedVisibility(
                visibleState = titleState,
                enter = fadeIn(tween(620)) + slideInVertically { it / 12 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.brand_line_one),
                        style = MaterialTheme.typography.displaySmall.copy(letterSpacing = 3.2.sp),
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.brand_line_two),
                        style = MaterialTheme.typography.displaySmall.copy(letterSpacing = 3.2.sp),
                        color = Accent,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            AnimatedVisibility(
                visibleState = taglineState,
                enter = fadeIn(tween(820)) + slideInVertically { it / 12 }
            ) {
                Text(
                    text = stringResource(R.string.splash_tagline),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.4.sp),
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .width(168.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Accent,
                trackColor = DividerSoft
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.splash_loading),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.0.sp
                ),
                color = TextMuted
            )
        }
    }
}

@Composable
private fun SplashBar(width: Int, color: Color, shift: Float) {
    Spacer(
        modifier = Modifier
            .offset(x = shift.dp)
            .width(width.dp)
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(color.copy(alpha = 0.20f))
    )
}
