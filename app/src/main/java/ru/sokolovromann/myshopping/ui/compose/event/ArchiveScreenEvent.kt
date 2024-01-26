package ru.sokolovromann.myshopping.ui.compose.event

import ru.sokolovromann.myshopping.ui.DrawerScreen

sealed class ArchiveScreenEvent {

    object OnShowBackScreen : ArchiveScreenEvent()

    data class OnShowProductsScreen(val shoppingUid: String) : ArchiveScreenEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : ArchiveScreenEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : ArchiveScreenEvent()

    object OnHideKeyboard : ArchiveScreenEvent()
}