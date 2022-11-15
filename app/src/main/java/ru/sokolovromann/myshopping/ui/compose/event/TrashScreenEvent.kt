package ru.sokolovromann.myshopping.ui.compose.event

sealed class TrashScreenEvent {

    object ShowBackScreen : TrashScreenEvent()

    data class ShowProducts(val uid: String) : TrashScreenEvent()

    object ShowPurchases : TrashScreenEvent()

    object ShowArchive : TrashScreenEvent()

    object ShowAutocompletes : TrashScreenEvent()

    object ShowSettings  : TrashScreenEvent()

    object ShowNavigationDrawer : TrashScreenEvent()

    object HideNavigationDrawer : TrashScreenEvent()
}