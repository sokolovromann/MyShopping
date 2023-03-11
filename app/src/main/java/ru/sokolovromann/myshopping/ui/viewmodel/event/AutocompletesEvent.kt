package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.state.AutocompleteLocation

sealed class AutocompletesEvent {

    object AddAutocomplete : AutocompletesEvent()

    data class ClearAutocomplete(val name: String) : AutocompletesEvent()

    data class DeleteAutocomplete(val name: String) : AutocompletesEvent()

    data class SelectNavigationItem(val route: UiRoute) : AutocompletesEvent()

    object SelectAutocompleteLocation : AutocompletesEvent()

    data class ShowAutocompletes(val location: AutocompleteLocation) : AutocompletesEvent()

    object ShowBackScreen : AutocompletesEvent()

    object ShowNavigationDrawer : AutocompletesEvent()

    data class ShowAutocompleteMenu(val uid: String) : AutocompletesEvent()

    object HideNavigationDrawer : AutocompletesEvent()

    object HideAutocompleteLocation : AutocompletesEvent()

    object HideAutocompleteMenu : AutocompletesEvent()
}