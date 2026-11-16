package com.focusblock.planner.presentation.app

import androidx.lifecycle.ViewModel
import com.focusblock.planner.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = mutableListOf<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash && current != target) {
            backStack.add(current)
        }
        _screen.value = target
        syncBackState()
    }

    fun back() {
        if (backStack.isEmpty()) return
        _screen.value = backStack.removeAt(backStack.lastIndex)
        syncBackState()
    }

    fun resetTo(target: Screen, root: Screen?) {
        backStack.clear()
        if (root != null) backStack.add(root)
        _screen.value = target
        syncBackState()
    }

    private fun syncBackState() {
        _canGoBack.value = backStack.isNotEmpty()
    }
}
