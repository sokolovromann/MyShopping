package ru.sokolovromann.myshopping.ui.compose.event

sealed class TrashScreenEvent {

    object ShowBackScreen : TrashScreenEvent()

    data class ShowProducts(val uid: String) : TrashScreenEvent()

    object ShowNavigationDrawer : TrashScreenEvent()

    object HideNavigationDrawer : TrashScreenEvent()
}