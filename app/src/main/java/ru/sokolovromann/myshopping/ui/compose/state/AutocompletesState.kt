package ru.sokolovromann.myshopping.ui.compose.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.utils.getAutocompleteItems

class AutocompletesState {

    var screenData by mutableStateOf(AutocompletesScreenData())
        private set

    fun showLoading() {
        screenData = AutocompletesScreenData(screenState = ScreenState.Loading)
    }

    fun showNotFound(preferences: AppPreferences, location: AutocompleteLocation) {
        screenData = AutocompletesScreenData(
            screenState = ScreenState.Nothing,
            location = location,
            fontSize = preferences.fontSize
        )
    }

    fun showAutocompletes(autocompletes: Autocompletes, location: AutocompleteLocation) {
        val preferences = autocompletes.preferences

        screenData = AutocompletesScreenData(
            screenState = ScreenState.Showing,
            autocompletes = autocompletes.getAutocompleteItems(),
            multiColumns = !preferences.smartphoneScreen,
            location = location,
            fontSize = preferences.fontSize
        )
    }

    fun showAutocompleteMenu(uid: String) {
        screenData = screenData.copy(autocompleteMenuUid = uid)
    }
    fun showLocation() {
        screenData = screenData.copy(showLocation = true)
    }

    fun hideAutocompleteMenu() {
        screenData = screenData.copy(autocompleteMenuUid = null)
    }

    fun hideLocation() {
        screenData = screenData.copy(showLocation = false)
    }
}

data class AutocompletesScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val autocompletes: Map<UiText, AutocompleteItems> = mapOf(),
    val autocompleteMenuUid: String? = null,
    val multiColumns: Boolean = false,
    val location: AutocompleteLocation = AutocompleteLocation.DefaultValue,
    val showLocation: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)