package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import ru.sokolovromann.myshopping.ui.UiRoute

sealed class ArchiveEvent {

    data class MoveShoppingListToPurchases(val uid: String) : ArchiveEvent()

    data class MoveShoppingListToTrash(val uid: String) : ArchiveEvent()

    object SelectShoppingListsSort : ArchiveEvent()

    object SelectShoppingListsDisplayCompleted : ArchiveEvent()

    object SelectShoppingListsDisplayTotal : ArchiveEvent()

    data class SelectNavigationItem(val route: UiRoute) : ArchiveEvent()

    data class SortShoppingLists(val sortBy: SortBy) : ArchiveEvent()

    data class DisplayShoppingListsCompleted(val displayCompleted: DisplayCompleted) : ArchiveEvent()

    data class DisplayShoppingListsTotal(val displayTotal: DisplayTotal) : ArchiveEvent()

    object InvertShoppingListsSort : ArchiveEvent()

    object ShowBackScreen : ArchiveEvent()

    data class ShowProducts(val uid: String) : ArchiveEvent()

    object ShowNavigationDrawer : ArchiveEvent()

    data class ShowShoppingListMenu(val uid: String) : ArchiveEvent()

    object HideNavigationDrawer : ArchiveEvent()

    object HideShoppingListMenu : ArchiveEvent()

    object HideShoppingListsSort : ArchiveEvent()

    object HideShoppingListsDisplayCompleted : ArchiveEvent()

    object HideShoppingListsDisplayTotal : ArchiveEvent()
}