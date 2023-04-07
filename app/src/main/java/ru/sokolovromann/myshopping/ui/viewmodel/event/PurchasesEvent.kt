package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import ru.sokolovromann.myshopping.ui.UiRoute

sealed class PurchasesEvent {

    object AddShoppingList : PurchasesEvent()

    data class MoveShoppingListToArchive(val uid: String) : PurchasesEvent()

    data class MoveShoppingListToTrash(val uid: String) : PurchasesEvent()

    data class MoveShoppingListUp(val uid: String) : PurchasesEvent()

    data class MoveShoppingListDown(val uid: String) : PurchasesEvent()

    object SelectDisplayPurchasesTotal : PurchasesEvent()

    data class SelectNavigationItem(val route: UiRoute) : PurchasesEvent()

    object SelectShoppingListsSort : PurchasesEvent()

    object SelectShoppingListsToArchive : PurchasesEvent()

    object SelectShoppingListsToTrash : PurchasesEvent()

    data class SortShoppingLists(val sortBy: SortBy) : PurchasesEvent()

    data class MoveAllShoppingListsTo(val toArchive: Boolean) : PurchasesEvent()

    data class MoveCompletedShoppingListsTo(val toArchive: Boolean) : PurchasesEvent()

    data class MoveActiveShoppingListsTo(val toArchive: Boolean) : PurchasesEvent()

    data class DisplayPurchasesTotal(val displayTotal: DisplayTotal) : PurchasesEvent()

    object DisplayHiddenShoppingLists : PurchasesEvent()

    data class ShowProducts(val uid: String) : PurchasesEvent()

    object ShowNavigationDrawer : PurchasesEvent()

    data class ShowShoppingListMenu(val uid: String) : PurchasesEvent()

    object ShowPurchasesMenu : PurchasesEvent()

    object HideNavigationDrawer : PurchasesEvent()

    object HideShoppingListMenu : PurchasesEvent()

    object HideDisplayPurchasesTotal : PurchasesEvent()

    object HidePurchasesMenu : PurchasesEvent()

    object HideShoppingListsSort : PurchasesEvent()

    object HideShoppingListsToArchive : PurchasesEvent()

    object HideShoppingListsToTrash : PurchasesEvent()

    object FinishApp : PurchasesEvent()
}