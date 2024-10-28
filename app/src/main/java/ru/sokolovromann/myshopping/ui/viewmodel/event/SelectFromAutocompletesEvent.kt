package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class SelectFromAutocompletesEvent {

    object OnClickSave : SelectFromAutocompletesEvent()

    object OnClickCancel : SelectFromAutocompletesEvent()

    data class OnAutocompleteSelected(val selected: Boolean, val name: String) : SelectFromAutocompletesEvent()
}