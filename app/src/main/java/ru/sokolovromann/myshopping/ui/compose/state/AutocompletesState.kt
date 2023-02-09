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

    fun showNotFound(preferences: AutocompletePreferences) {
        screenData = AutocompletesScreenData(
            screenState = ScreenState.Nothing,
            fontSize = preferences.fontSize
        )
    }

    fun showAutocompletes(autocompletes: Autocompletes) {
        val preferences = autocompletes.preferences

        screenData = AutocompletesScreenData(
            screenState = ScreenState.Showing,
            autocompletes = autocompletes.getAutocompleteItems(),
            multiColumns = preferences.screenSize == ScreenSize.TABLET,
            fontSize = preferences.fontSize
        )
    }

    fun showAutocompleteMenu(uid: String) {
        screenData = screenData.copy(autocompleteMenuUid = uid)
    }

    fun hideAutocompleteMenu() {
        screenData = screenData.copy(autocompleteMenuUid = null)
    }
}

data class AutocompletesScreenData(
    val screenState: ScreenState = ScreenState.Nothing,
    val autocompletes: List<AutocompleteItem> = listOf(),
    val autocompleteMenuUid: String? = null,
    val multiColumns: Boolean = false,
    val fontSize: FontSize = FontSize.MEDIUM
)