package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import ru.sokolovromann.myshopping.ui.UiRoute

sealed class ArchiveEvent {

    data class MoveShoppingListToPurchases(val uid: String) : ArchiveEvent()

    data class MoveShoppingListToTrash(val uid: String) : ArchiveEvent()

    object SelectDisplayPurchasesTotal : ArchiveEvent()

    data class SelectNavigationItem(val route: UiRoute) : ArchiveEvent()

    object SelectShoppingListsSort : ArchiveEvent()

    data class SortShoppingLists(val sortBy: SortBy) : ArchiveEvent()

    data class DisplayPurchasesTotal(val displayTotal: DisplayTotal) : ArchiveEvent()

    object DisplayHiddenShoppingLists : ArchiveEvent()

    object ShowBackScreen : ArchiveEvent()

    data class ShowProducts(val uid: String) : ArchiveEvent()

    object ShowNavigationDrawer : ArchiveEvent()

    data class ShowShoppingListMenu(val uid: String) : ArchiveEvent()

    object ShowArchiveMenu : ArchiveEvent()

    object HideNavigationDrawer : ArchiveEvent()

    object HideShoppingListMenu : ArchiveEvent()

    object HideDisplayPurchasesTotal : ArchiveEvent()

    object HideArchiveMenu : ArchiveEvent()

    object HideShoppingListsSort : ArchiveEvent()
}