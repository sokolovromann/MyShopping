package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import ru.sokolovromann.myshopping.ui.UiRoute

sealed class ArchiveEvent {

    object MoveShoppingListsToPurchases : ArchiveEvent()

    object MoveShoppingListsToTrash : ArchiveEvent()

    object SelectDisplayPurchasesTotal : ArchiveEvent()

    data class SelectNavigationItem(val route: UiRoute) : ArchiveEvent()

    object SelectShoppingListsSort : ArchiveEvent()

    data class SelectShoppingList(val uid: String) : ArchiveEvent()

    object SelectSelectShoppingLists : ArchiveEvent()

    object SelectAllShoppingLists : ArchiveEvent()

    object SelectCompletedShoppingLists : ArchiveEvent()

    object SelectActiveShoppingLists : ArchiveEvent()

    data class UnselectShoppingList(val uid: String) : ArchiveEvent()

    object CancelSelectingShoppingLists : ArchiveEvent()

    data class SortShoppingLists(val sortBy: SortBy) : ArchiveEvent()

    data class DisplayPurchasesTotal(val displayTotal: DisplayTotal) : ArchiveEvent()

    object DisplayHiddenShoppingLists : ArchiveEvent()

    object ShowBackScreen : ArchiveEvent()

    data class ShowProducts(val uid: String) : ArchiveEvent()

    object ShowNavigationDrawer : ArchiveEvent()

    object ShowArchiveMenu : ArchiveEvent()

    object HideNavigationDrawer : ArchiveEvent()

    object HideDisplayPurchasesTotal : ArchiveEvent()

    object HideArchiveMenu : ArchiveEvent()

    object HideShoppingListsSort : ArchiveEvent()

    object HideSelectShoppingLists : ArchiveEvent()
}