package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import ru.sokolovromann.myshopping.ui.UiRoute

sealed class PurchasesEvent {

    object AddShoppingList : PurchasesEvent()

    data class MoveShoppingListToArchive(val uid: String) : PurchasesEvent()

    data class MoveShoppingListToTrash(val uid: String) : PurchasesEvent()

    object SelectShoppingListsSort : PurchasesEvent()

    object SelectShoppingListsDisplayCompleted : PurchasesEvent()

    object SelectShoppingListsDisplayTotal : PurchasesEvent()

    data class SelectNavigationItem(val route: UiRoute) : PurchasesEvent()

    data class SortShoppingLists(val sortBy: SortBy) : PurchasesEvent()

    data class DisplayShoppingListsCompleted(val displayCompleted: DisplayCompleted) : PurchasesEvent()

    data class DisplayShoppingListsTotal(val displayTotal: DisplayTotal) : PurchasesEvent()

    object InvertShoppingListsSort : PurchasesEvent()

    data class ShowProducts(val uid: String) : PurchasesEvent()

    object ShowNavigationDrawer : PurchasesEvent()

    data class ShowShoppingListMenu(val uid: String) : PurchasesEvent()

    object HideNavigationDrawer : PurchasesEvent()

    object HideShoppingListMenu : PurchasesEvent()

    object HideShoppingListsSort : PurchasesEvent()

    object HideShoppingListsDisplayCompleted : PurchasesEvent()

    object HideShoppingListsDisplayTotal : PurchasesEvent()

    object FinishApp : PurchasesEvent()
}