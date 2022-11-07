package ru.sokolovromann.myshopping.ui.compose.event

sealed class PurchasesScreenEvent {

    data class ShowProducts(val uid: String) : PurchasesScreenEvent()

    object ShowNavigationDrawer : PurchasesScreenEvent()

    object HideNavigationDrawer : PurchasesScreenEvent()

    object FinishApp : PurchasesScreenEvent()
}