package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.model.SettingsWithConfig

class MaxAutocompletesState {

    var maxNames: Int by mutableIntStateOf(0)
        private set

    var maxQuantities: Int by mutableIntStateOf(0)
        private set

    var maxMoneys: Int by mutableIntStateOf(0)
        private set

    var maxOthers: Int by mutableIntStateOf(0)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(settingsWithConfig: SettingsWithConfig) {
        val userPreferences = settingsWithConfig.appConfig.userPreferences
        maxNames = userPreferences.maxAutocompletesNames
        maxQuantities = userPreferences.maxAutocompletesQuantities
        maxMoneys = userPreferences.maxAutocompletesMoneys
        maxOthers = userPreferences.maxAutocompletesOthers
        waiting = false
    }

    fun onMaxNamesChanged(value: Int) {
        maxNames = value
    }

    fun onMaxQuantitiesChanged(value: Int) {
        maxQuantities = value
    }

    fun onMaxMoneysChanged(value: Int) {
        maxMoneys = value
    }

    fun onMaxOthersChanged(value: Int) {
        maxOthers = value
    }

    fun onWaiting() {
        waiting = true
    }
}