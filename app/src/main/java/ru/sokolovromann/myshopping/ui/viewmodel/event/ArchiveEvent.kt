package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.ui.UiRoute

sealed class ArchiveEvent {

    data class MoveShoppingListToPurchases(val uid: String) : ArchiveEvent()

    data class MoveShoppingListToTrash(val uid: String) : ArchiveEvent()

    object SelectShoppingListsDisplayTotal : ArchiveEvent()

    data class SelectNavigationItem(val route: UiRoute) : ArchiveEvent()

    data class DisplayShoppingListsTotal(val displayTotal: DisplayTotal) : ArchiveEvent()

    object ShowBackScreen : ArchiveEvent()

    data class ShowProducts(val uid: String) : ArchiveEvent()

    object ShowNavigationDrawer : ArchiveEvent()

    data class ShowShoppingListMenu(val uid: String) : ArchiveEvent()

    object HideNavigationDrawer : ArchiveEvent()

    object HideShoppingListMenu : ArchiveEvent()

    object HideShoppingListsDisplayTotal : ArchiveEvent()
}