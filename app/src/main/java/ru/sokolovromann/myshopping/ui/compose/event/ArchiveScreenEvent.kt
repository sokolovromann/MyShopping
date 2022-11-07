package ru.sokolovromann.myshopping.ui.compose.event

sealed class ArchiveScreenEvent {

    object ShowBackScreen : ArchiveScreenEvent()

    data class ShowProducts(val uid: String) : ArchiveScreenEvent()

    object ShowNavigationDrawer : ArchiveScreenEvent()

    object HideNavigationDrawer : ArchiveScreenEvent()
}