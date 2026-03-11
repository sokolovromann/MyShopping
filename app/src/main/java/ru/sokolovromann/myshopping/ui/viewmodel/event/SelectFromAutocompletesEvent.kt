package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.utils.UID

sealed class SelectFromAutocompletesEvent {

    object OnClickSave : SelectFromAutocompletesEvent()

    object OnClickCancel : SelectFromAutocompletesEvent()

    data class OnAutocompleteSelected(val selected: Boolean, val uid: UID) : SelectFromAutocompletesEvent()
}