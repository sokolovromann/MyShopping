package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.getAutocompleteItems

class AutocompletesState {

    private var autocompletes by mutableStateOf(Autocompletes())

    var screenData by mutableStateOf(AutocompletesScreenData())
        private set

    fun showLoading() {
        screenData = AutocompletesScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(autocompletes: Autocompletes, location: AutocompleteLocation) {
        this.autocompletes = autocompletes

        screenData = AutocompletesScreenData(
            screenState = ScreenState.Nothing,
            smartphoneScreen = autocompletes.isSmartphoneScreen(),
            location = location,
            locationEnabled = autocompletes.isLocationEnabled(),
            fontSize = autocompletes.getFontSize()
        )
    }

    fun showAutocompletes(autocompletes: Autocompletes, location: AutocompleteLocation) {
        this.autocompletes = autocompletes

        screenData = AutocompletesScreenData(
            screenState = ScreenState.Showing,
            autocompletes = autocompletes.getAutocompleteItems(),
            multiColumns = autocompletes.isMultiColumns(),
            smartphoneScreen = autocompletes.isSmartphoneScreen(),
            location = location,
            locationEnabled = autocompletes.isLocationEnabled(),
            fontSize = autocompletes.getFontSize()
        )
    }

    fun showLocation() {
        screenData = screenData.copy(showLocation = true)
    }

    fun hideLocation() {
        screenData = screenData.copy(showLocation = false)
    }

    fun selectAllAutocompletes() {
        screenData = screenData.copy(selectedNames = autocompletes.getNames())
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