package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.ui.UiRoute

sealed class PurchasesEvent {

    object AddShoppingList : PurchasesEvent()

    data class MoveShoppingListToArchive(val uid: String) : PurchasesEvent()

    data class MoveShoppingListToTrash(val uid: String) : PurchasesEvent()

    object SelectShoppingListsSort : PurchasesEvent()

    object SelectShoppingListsDisplayCompleted : PurchasesEvent()

    object SelectShoppingListsDisplayTotal : PurchasesEvent()

    data class SelectNavigationItem(val route: UiRoute) : PurchasesEvent()

    object SortShoppingListsByCreated : PurchasesEvent()

    object SortShoppingListsByLastModified : PurchasesEvent()

    object SortShoppingListsByName : PurchasesEvent()

    object SortShoppingListsByTotal : PurchasesEvent()

    object DisplayShoppingListsCompletedFirst : PurchasesEvent()

    object DisplayShoppingListsCompletedLast : PurchasesEvent()

    object DisplayShoppingListsAllTotal : PurchasesEvent()

    object DisplayShoppingListsCompletedTotal : PurchasesEvent()

    object DisplayShoppingListsActiveTotal : PurchasesEvent()

    object InvertShoppingListsSort : PurchasesEvent()

    data class ShowProducts(val uid: String) : PurchasesEvent()

    object ShowNavigationDrawer : PurchasesEvent()

    data class ShowShoppingListMenu(val uid: String) : PurchasesEvent()

    object HideShoppingListsCompleted : PurchasesEvent()

    object HideNavigationDrawer : PurchasesEvent()

    object HideShoppingListMenu : PurchasesEvent()

    object HideShoppingListsSort : PurchasesEvent()

    object HideShoppingListsDisplayCompleted : PurchasesEvent()

    object HideShoppingListsDisplayTotal : PurchasesEvent()

    object FinishApp : PurchasesEvent()
}