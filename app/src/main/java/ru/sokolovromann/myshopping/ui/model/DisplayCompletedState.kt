package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.DisplayCompleted

class DisplayCompletedState {

    var appDisplayCompletedValue: SelectedValue<DisplayCompleted> by mutableStateOf(SelectedValue(DisplayCompleted.DefaultValue))
        private set

    var expandedAppDisplayCompleted: Boolean by mutableStateOf(false)
        private set

    var widgetDisplayCompletedValue: SelectedValue<DisplayCompleted> by mutableStateOf(SelectedValue(DisplayCompleted.DefaultValue))
        private set

    var expandedWidgetDisplayCompleted: Boolean by mutableStateOf(false)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(appConfig: AppConfig) {
        val userPreferences = appConfig.userPreferences

        appDisplayCompletedValue = toDisplayCompletedValue(userPreferences.appDisplayCompleted)
        expandedAppDisplayCompleted = false
        widgetDisplayCompletedValue = toDisplayCompletedValue(userPreferences.widgetDisplayCompleted)
        expandedWidgetDisplayCompleted = false
        waiting = false
    }

    fun onWaiting() {
        waiting = true
    }

    fun onSelectAppDisplayCompleted(expanded: Boolean) {
        expandedAppDisplayCompleted = expanded
    }

    fun onAppDisplayCompletedSelected(displayCompleted: DisplayCompleted) {
        appDisplayCompletedValue = toDisplayCompletedValue(displayCompleted)
        expandedAppDisplayCompleted = false
    }

    fun onSelectWidgetDisplayCompleted(expanded: Boolean) {
        expandedWidgetDisplayCompleted = expanded
    }

    fun onWidgetDisplayCompletedSelected(displayCompleted: DisplayCompleted) {
        widgetDisplayCompletedValue = toDisplayCompletedValue(displayCompleted)
        expandedWidgetDisplayCompleted = false
    }

    private fun toDisplayCompletedValue(displayCompleted: DisplayCompleted): SelectedValue<DisplayCompleted> {
        return SelectedValue(
            selected = displayCompleted,
            text = when (displayCompleted) {
                DisplayCompleted.FIRST -> UiString.FromResources(R.string.displayCompleted_action_displayCompletedFirst)
                DisplayCompleted.LAST -> UiString.FromResources(R.string.displayCompleted_action_displayCompletedLast)
                DisplayCompleted.HIDE -> UiString.FromResources(R.string.displayCompleted_action_hideCompleted)
                DisplayCompleted.NO_SPLIT -> UiString.FromResources(R.string.displayCompleted_action_noSplitCompleted)
            }
        )
    }
}