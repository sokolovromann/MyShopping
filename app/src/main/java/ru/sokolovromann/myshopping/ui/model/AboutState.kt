package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig

class AboutState {

    var appVersion by mutableStateOf("")
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(settingsWithConfig: SettingsWithConfig) {
        appVersion = settingsWithConfig.appConfig.appBuildConfig.getDisplayName()
        waiting = false
    }

    fun onWaiting() {
        waiting = true
    }
}