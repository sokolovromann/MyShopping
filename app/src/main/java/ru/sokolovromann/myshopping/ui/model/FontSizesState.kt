package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.AppConfig
import ru.sokolovromann.myshopping.data.model.FontSize

class FontSizesState {

    var appFontSizeValue: SelectedValue<FontSize> by mutableStateOf(SelectedValue(FontSize.DefaultValue))
        private set

    var expandedAppFontSize: Boolean by mutableStateOf(false)
        private set

    var widgetFontSizeValue: SelectedValue<FontSize> by mutableStateOf(SelectedValue(FontSize.DefaultValue))
        private set

    var expandedWidgetFontSize: Boolean by mutableStateOf(false)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(appConfig: AppConfig) {
        val userPreferences = appConfig.userPreferences

        appFontSizeValue = toFontSizeValue(userPreferences.appFontSize)
        expandedAppFontSize = false
        widgetFontSizeValue = toFontSizeValue(userPreferences.widgetFontSize)
        expandedWidgetFontSize = false
        waiting = false
    }

    fun onWaiting() {
        waiting = true
    }

    fun onSelectAppFontSize(expanded: Boolean) {
        expandedAppFontSize = expanded
    }

    fun onAppFontSizeSelected(fontSize: FontSize) {
        appFontSizeValue = toFontSizeValue(fontSize)
        expandedAppFontSize = false
    }

    fun onSelectWidgetFontSize(expanded: Boolean) {
        expandedWidgetFontSize = expanded
    }

    fun onWidgetFontSizeSelected(fontSize: FontSize) {
        widgetFontSizeValue = toFontSizeValue(fontSize)
        expandedWidgetFontSize = false
    }

    private fun toFontSizeValue(fontSize: FontSize): SelectedValue<FontSize> {
        return SelectedValue(
            selected = fontSize,
            text = when (fontSize) {
                FontSize.SMALL -> UiString.FromResources(R.string.fontSizes_action_selectSmallFontSize)
                FontSize.MEDIUM -> UiString.FromResources(R.string.fontSizes_action_selectMediumFontSize)
                FontSize.LARGE -> UiString.FromResources(R.string.fontSizes_action_selectLargeFontSize)
                FontSize.VERY_LARGE -> UiString.FromResources(R.string.fontSizes_action_selectVeryLargeFontSize)
                FontSize.HUGE -> UiString.FromResources(R.string.fontSizes_action_selectHugeFontSize)
                FontSize.VERY_HUGE -> UiString.FromResources(R.string.fontSizes_action_selectVeryHugeFontSize)
            }
        )
    }
}