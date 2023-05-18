package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import ru.sokolovromann.myshopping.ui.UiRoute

sealed class PurchasesEvent {

    object AddShoppingList : PurchasesEvent()

    object MoveShoppingListsToArchive : PurchasesEvent()

    object MoveShoppingListsToTrash : PurchasesEvent()

    data class MoveShoppingListUp(val uid: String) : PurchasesEvent()

    data class MoveShoppingListDown(val uid: String) : PurchasesEvent()

    object SelectDisplayPurchasesTotal : PurchasesEvent()

    data class SelectNavigationItem(val route: UiRoute) : PurchasesEvent()

    object SelectShoppingListsSort : PurchasesEvent()

    data class SelectShoppingList(val uid: String) : PurchasesEvent()

    object SelectAllShoppingLists : PurchasesEvent()

    data class UnselectShoppingList(val uid: String) : PurchasesEvent()

    object CancelSelectingShoppingLists : PurchasesEvent()

    data class SortShoppingLists(val sortBy: SortBy) : PurchasesEvent()

    object ReverseSortShoppingLists : PurchasesEvent()

    data class DisplayPurchasesTotal(val displayTotal: DisplayTotal) : PurchasesEvent()

    object DisplayHiddenShoppingLists : PurchasesEvent()

    data class ShowProducts(val uid: String) : PurchasesEvent()

    object ShowNavigationDrawer : PurchasesEvent()

    object ShowPurchasesMenu : PurchasesEvent()

    object HideNavigationDrawer : PurchasesEvent()

    object HideDisplayPurchasesTotal : PurchasesEvent()

    object HidePurchasesMenu : PurchasesEvent()

    object HideShoppingListsSort : PurchasesEvent()

    object FinishApp : PurchasesEvent()
}