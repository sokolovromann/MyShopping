package ru.sokolovromann.myshopping.ui.compose.event

import ru.sokolovromann.myshopping.ui.DrawerScreen

sealed class PurchasesScreenEvent {

    object OnFinishApp : PurchasesScreenEvent()

    data class OnShowProductsScreen(val shoppingUid: String) : PurchasesScreenEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : PurchasesScreenEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : PurchasesScreenEvent()

    object OnHideKeyboard : PurchasesScreenEvent()
}