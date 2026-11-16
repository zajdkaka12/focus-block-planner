package com.focusblock.planner.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.focusblock.planner.presentation.app.AppViewModel
import com.focusblock.planner.presentation.blockeditor.BlockEditorViewModel
import com.focusblock.planner.presentation.categories.CategoriesViewModel
import com.focusblock.planner.presentation.game.GameViewModel
import com.focusblock.planner.presentation.gameover.GameOverViewModel
import com.focusblock.planner.presentation.menu.MenuViewModel
import com.focusblock.planner.presentation.settings.SettingsViewModel
import com.focusblock.planner.presentation.splash.SplashViewModel
import com.focusblock.planner.presentation.weeklyreview.WeeklyReviewViewModel

object AppViewModelFactory {

    val factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { AppViewModel() }
        initializer { SplashViewModel() }
        initializer {
            MenuViewModel(
                ServiceLocator.planRepository,
                ServiceLocator.categoryRepository,
                ServiceLocator.statsRepository
            )
        }
        initializer {
            GameViewModel(
                ServiceLocator.planRepository,
                ServiceLocator.categoryRepository,
                ServiceLocator.settingsRepository,
                ServiceLocator.statsRepository,
                ServiceLocator.moveBlock,
                ServiceLocator.completeBlock,
                ServiceLocator.buildDaySummary
            )
        }
        initializer {
            BlockEditorViewModel(
                ServiceLocator.planRepository,
                ServiceLocator.categoryRepository,
                ServiceLocator.settingsRepository,
                ServiceLocator.placeBlock
            )
        }
        initializer {
            CategoriesViewModel(
                ServiceLocator.categoryRepository,
                ServiceLocator.statsRepository,
                ServiceLocator.buildWeekStats
            )
        }
        initializer {
            WeeklyReviewViewModel(
                ServiceLocator.categoryRepository,
                ServiceLocator.statsRepository,
                ServiceLocator.buildWeekStats
            )
        }
        initializer {
            SettingsViewModel(
                ServiceLocator.settingsRepository,
                ServiceLocator.planRepository
            )
        }
        initializer { GameOverViewModel(ServiceLocator.planRepository) }
    }
}
