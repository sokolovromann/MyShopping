package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.model.AutocompleteLocation

sealed class AutocompletesEvent {

    object AddAutocomplete : AutocompletesEvent()

    object ClearAutocompletes : AutocompletesEvent()

    object DeleteAutocompletes : AutocompletesEvent()

    data class SelectNavigationItem(val route: UiRoute) : AutocompletesEvent()

    object SelectAutocompleteLocation : AutocompletesEvent()

    object SelectAllAutocompletes : AutocompletesEvent()

    data class SelectAutocomplete(val name: String) : AutocompletesEvent()

    data class UnselectAutocomplete(val name: String) : AutocompletesEvent()

    object CancelSelectingAutocompletes : AutocompletesEvent()

    data class ShowAutocompletes(val location: AutocompleteLocation) : AutocompletesEvent()

    object ShowBackScreen : AutocompletesEvent()

    object ShowNavigationDrawer : AutocompletesEvent()

    object HideNavigationDrawer : AutocompletesEvent()

    object HideAutocompleteLocation : AutocompletesEvent()
}