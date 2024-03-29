package ru.sokolovromann.myshopping.ui.compose.event

import ru.sokolovromann.myshopping.ui.DrawerScreen

sealed class TrashScreenEvent {

    object OnShowBackScreen : TrashScreenEvent()

    data class OnShowProductsScreen(val shoppingUid: String) : TrashScreenEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : TrashScreenEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : TrashScreenEvent()
}