package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.ui.UiRoute

sealed class AutocompletesEvent {

    object AddAutocomplete : AutocompletesEvent()

    data class EditAutocomplete(val uid: String) : AutocompletesEvent()

    data class DeleteAutocomplete(val uid: String) : AutocompletesEvent()

    object SelectAutocompletesSort : AutocompletesEvent()

    data class SelectNavigationItem(val route: UiRoute) : AutocompletesEvent()

    object SortAutocompletesByCreated : AutocompletesEvent()

    object SortAutocompletesByName : AutocompletesEvent()

    object InvertAutocompletesSort : AutocompletesEvent()

    object ShowBackScreen : AutocompletesEvent()

    object ShowNavigationDrawer : AutocompletesEvent()

    data class ShowAutocompleteMenu(val uid: String) : AutocompletesEvent()

    object HideNavigationDrawer : AutocompletesEvent()

    object HideAutocompleteMenu : AutocompletesEvent()

    object HideAutocompletesSort : AutocompletesEvent()
}