package ru.sokolovromann.myshopping.ui.compose.event

sealed class AutocompletesScreenEvent {

    object AddAutocomplete : AutocompletesScreenEvent()

    data class EditAutocomplete(val uid: String) : AutocompletesScreenEvent()

    object ShowBackScreen : AutocompletesScreenEvent()

    object ShowPurchases : AutocompletesScreenEvent()

    object ShowArchive : AutocompletesScreenEvent()

    object ShowTrash : AutocompletesScreenEvent()

    object ShowSettings : AutocompletesScreenEvent()

    object ShowNavigationDrawer : AutocompletesScreenEvent()

    object HideNavigationDrawer : AutocompletesScreenEvent()
}