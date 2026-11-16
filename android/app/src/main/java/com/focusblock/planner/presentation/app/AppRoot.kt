package com.focusblock.planner.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.focusblock.planner.core.di.AppViewModelFactory
import com.focusblock.planner.core.navigation.Screen
import com.focusblock.planner.core.theme.BgDeep
import com.focusblock.planner.presentation.blockeditor.BlockEditorRoute
import com.focusblock.planner.presentation.categories.CategoriesRoute
import com.focusblock.planner.presentation.game.GameRoute
import com.focusblock.planner.presentation.gameover.GameOverRoute
import com.focusblock.planner.presentation.menu.MenuRoute
import com.focusblock.planner.presentation.settings.SettingsRoute
import com.focusblock.planner.presentation.splash.SplashRoute
import com.focusblock.planner.presentation.weeklyreview.WeeklyReviewRoute

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.factory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) { appViewModel.back() }

    Box(modifier = Modifier.fillMaxSize().background(BgDeep)) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(140)) },
            label = "appScreen"
        ) { target ->
            when (target) {
                is Screen.Splash -> SplashRoute(
                    onFinished = { appViewModel.navigate(Screen.Menu) }
                )

                is Screen.Menu -> MenuRoute(
                    onStart = { appViewModel.navigate(Screen.Game) },
                    onCategories = { appViewModel.navigate(Screen.Categories) },
                    onWeekly = { appViewModel.navigate(Screen.WeeklyReview) },
                    onSettings = { appViewModel.navigate(Screen.Settings) }
                )

                is Screen.Game -> GameRoute(
                    onBack = { appViewModel.back() },
                    onEditBlock = { blockId, startMinutes ->
                        appViewModel.navigate(Screen.BlockEditor(blockId, startMinutes))
                    },
                    onDayComplete = { summary ->
                        appViewModel.resetTo(Screen.GameOver(summary), Screen.Menu)
                    }
                )

                is Screen.BlockEditor -> BlockEditorRoute(
                    blockId = target.blockId,
                    startMinutes = target.startMinutes,
                    onClose = { appViewModel.back() }
                )

                is Screen.Categories -> CategoriesRoute(
                    onBack = { appViewModel.back() }
                )

                is Screen.WeeklyReview -> WeeklyReviewRoute(
                    onBack = { appViewModel.back() }
                )

                is Screen.Settings -> SettingsRoute(
                    onBack = { appViewModel.back() }
                )

                is Screen.GameOver -> GameOverRoute(
                    summary = target.summary,
                    onPlanAgain = { appViewModel.resetTo(Screen.Game, Screen.Menu) },
                    onMenu = { appViewModel.resetTo(Screen.Menu, null) }
                )
            }
        }
    }
}
