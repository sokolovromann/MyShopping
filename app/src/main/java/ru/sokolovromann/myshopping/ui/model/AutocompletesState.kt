package ru.sokolovromann.myshopping.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.app.AppLocale
import ru.sokolovromann.myshopping.data.model.AutocompletesWithConfig
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.ui.model.mapper.UiAppConfigMapper
import ru.sokolovromann.myshopping.ui.model.mapper.UiAutocompletesMapper

class AutocompletesState {

    private var autocompletesWithConfig by mutableStateOf(AutocompletesWithConfig())

    var autocompletes: List<AutocompleteItem> by mutableStateOf(listOf())
        private set

    var selectedNames: List<String>? by mutableStateOf(null)
        private set

    var multiColumns: Boolean by mutableStateOf(false)
        private set

    var deviceSize: DeviceSize by mutableStateOf(DeviceSize.DefaultValue)
        private set

    var locationValue: SelectedValue<AutocompleteLocation> by mutableStateOf(SelectedValue(AutocompleteLocation.DefaultValue))
        private set

    var expandedLocation: Boolean by mutableStateOf(false)
        private set

    var locationEnabled: Boolean by mutableStateOf(true)
        private set

    var fontSize: UiFontSize by mutableStateOf(UiFontSize.Default)
        private set

    var waiting: Boolean by mutableStateOf(true)
        private set

    fun populate(autocompletesWithConfig: AutocompletesWithConfig, location: AutocompleteLocation) {
        this.autocompletesWithConfig = autocompletesWithConfig

        val userPreferences = autocompletesWithConfig.appConfig.userPreferences
        autocompletes = UiAutocompletesMapper.toAutocompleteItems(autocompletesWithConfig)
        selectedNames = null
        deviceSize = autocompletesWithConfig.appConfig.deviceConfig.getDeviceSize()
        multiColumns = deviceSize == DeviceSize.Large
        locationValue = UiAutocompletesMapper.toLocationValue(location)
        expandedLocation = false
        locationEnabled = AppLocale.isLanguageSupported() && userPreferences.displayDefaultAutocompletes
        fontSize = UiAppConfigMapper.toUiFontSize(userPreferences.fontSize)
        waiting = false
    }

    fun onSelectLocation(expanded: Boolean) {
        expandedLocation = expanded
    }

    fun onAllAutocompletesSelected(selected: Boolean) {
        selectedNames = if (selected) autocompletesWithConfig.getNames() else null
    }

    fun onAutocompleteSelected(selected: Boolean, name: String) {
        val names = (selectedNames?.toMutableList() ?: mutableListOf()).apply {
            if (selected) add(name) else remove(name)
        }
        selectedNames = if (names.isEmpty()) null else names
    }

    fun onWaiting() {
        waiting = true
    }

    fun isNotFound(): Boolean {
        return autocompletesWithConfig.isEmpty()
    }
}