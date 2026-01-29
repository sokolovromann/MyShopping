package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.ui.DrawerScreen
import ru.sokolovromann.myshopping.utils.UID

sealed class AutocompletesEvent {

    object OnClickAddAutocomplete : AutocompletesEvent()

    object OnClickClearAutocompletes : AutocompletesEvent()

    object OnClickDeleteAutocompletes : AutocompletesEvent()

    object OnClickBack : AutocompletesEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : AutocompletesEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : AutocompletesEvent()

    data class OnAllAutocompletesSelected(val selected: Boolean) : AutocompletesEvent()

    data class OnAutocompleteSelected(val selected: Boolean, val uid: UID) : AutocompletesEvent()
}