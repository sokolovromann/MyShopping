package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.app.AppLocale
import ru.sokolovromann.myshopping.data.model.AutocompletesWithConfig
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.getAutocompletesItems

class AutocompletesState {

    private var autocompletesWithConfig by mutableStateOf(AutocompletesWithConfig())

    var screenData by mutableStateOf(AutocompletesScreenData())
        private set

    fun showLoading() {
        screenData = AutocompletesScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(autocompletesWithConfig: AutocompletesWithConfig, location: AutocompleteLocation) {
        this.autocompletesWithConfig = autocompletesWithConfig

        screenData = AutocompletesScreenData(
            screenState = ScreenState.Nothing,
            smartphoneScreen = isSmartphoneScreen(),
            location = location,
            locationEnabled = isLocationEnabled(),
            fontSize = autocompletesWithConfig.appConfig.userPreferences.fontSize
        )
    }

    fun showAutocompletes(autocompletesWithConfig: AutocompletesWithConfig, location: AutocompleteLocation) {
        this.autocompletesWithConfig = autocompletesWithConfig

        val userPreferences = autocompletesWithConfig.appConfig.userPreferences
        screenData = AutocompletesScreenData(
            screenState = ScreenState.Showing,
            autocompletes = autocompletesWithConfig.getAutocompletesItems(),
            multiColumns = isMultiColumns(),
            smartphoneScreen = isSmartphoneScreen(),
            location = location,
            locationEnabled = isLocationEnabled(),
            fontSize = userPreferences.fontSize
        )
    }

    fun showLocation() {
        screenData = screenData.copy(showLocation = true)
    }

    fun hideLocation() {
        screenData = screenData.copy(showLocation = false)
    }

    fun selectAllAutocompletes() {
        screenData = screenData.copy(selectedNames = autocompletesWithConfig.getNames())
    }

    fun selectAutocomplete(name: String) {
        val names = (screenData.selectedNames?.toMutableList() ?: mutableListOf())
            .apply { add(name) }
        screenData = screenData.copy(selectedNames = names)
    }

    fun unselectAutocomplete(name: String) {
        val names = (screenData.selectedNames?.toMutableList() ?: mutableListOf())
            .apply { remove(name) }
        val checkedNames = if (names.isEmpty()) null else names
        screenData = screenData.copy(selectedNames = checkedNames)
    }

    fun unselectAllAutocompletes() {
        screenData = screenData.copy(selectedNames = null)
    }

    private fun isLocationEnabled(): Boolean {
        return AppLocale.isLanguageSupported() && autocompletesWithConfig.appConfig.userPreferences.displayDefaultAutocompletes
    }

    private fun isSmartphoneScreen(): Boolean {
        return autocompletesWithConfig.appConfig.deviceConfig.getDeviceSize() == DeviceSize.Medium
    }

    private fun isMultiColumns(): Boolean {
        return autocompletesWithConfig.appConfig.deviceConfig.getDeviceSize() == DeviceSize.Large
    }
}

data class AutocompletesScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val autocompletes: Map<UiText, AutocompleteItems> = mapOf(),
    val multiColumns: Boolean = false,
    val smartphoneScreen: Boolean = true,
    val location: AutocompleteLocation = AutocompleteLocation.DefaultValue,
    val showLocation: Boolean = false,
    val locationEnabled: Boolean = true,
    val fontSize: FontSize = FontSize.MEDIUM,
    val selectedNames: List<String>? = null
)