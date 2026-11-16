package com.focusblock.planner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.focusblock.planner.core.di.ServiceLocator
import com.focusblock.planner.core.theme.AppTheme
import com.focusblock.planner.presentation.app.AppRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        ServiceLocator.init(applicationContext)
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppRoot()
            }
        }
    }
}
