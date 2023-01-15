package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.MainPreferences

class MainState {

    var loading by mutableStateOf(false)
        private set

    var nightTheme by mutableStateOf(false)
        private set

    fun showLoading() {
        loading = true
    }

    fun showContent() {
        loading = false
    }

    fun applyPreferences(preferences: MainPreferences) {
        nightTheme = preferences.nightTheme
    }
}