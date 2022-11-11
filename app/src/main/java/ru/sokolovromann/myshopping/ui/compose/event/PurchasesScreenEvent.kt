package ru.sokolovromann.myshopping.ui.compose.event

sealed class PurchasesScreenEvent {

    data class ShowProducts(val uid: String) : PurchasesScreenEvent()

    object ShowArchive : PurchasesScreenEvent()

    object ShowTrash : PurchasesScreenEvent()

    object ShowAutocompletes : PurchasesScreenEvent()

    object ShowSettings : PurchasesScreenEvent()

    object ShowNavigationDrawer : PurchasesScreenEvent()

    object HideNavigationDrawer : PurchasesScreenEvent()

    object FinishApp : PurchasesScreenEvent()
}