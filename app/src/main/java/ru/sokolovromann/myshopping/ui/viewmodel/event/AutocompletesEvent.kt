package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.ui.DrawerScreen
import ru.sokolovromann.myshopping.ui.model.AutocompleteLocation

sealed class AutocompletesEvent {

    object OnClickAdd : AutocompletesEvent()

    object OnClickClear : AutocompletesEvent()

    object OnClickDelete : AutocompletesEvent()

    object OnClickBack : AutocompletesEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : AutocompletesEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : AutocompletesEvent()

    data class OnLocationSelected(val location: AutocompleteLocation) : AutocompletesEvent()

    data class OnSelectLocation(val expanded: Boolean) : AutocompletesEvent()

    data class OnAllAutocompletesSelected(val selected: Boolean) : AutocompletesEvent()

    data class OnAutocompleteSelected(val selected: Boolean, val name: String) : AutocompletesEvent()
}