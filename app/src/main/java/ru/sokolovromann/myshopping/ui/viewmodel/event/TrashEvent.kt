package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import ru.sokolovromann.myshopping.ui.UiRoute

sealed class TrashEvent {

    data class MoveShoppingListToPurchases(val uid: String) : TrashEvent()

    data class MoveShoppingListToArchive(val uid: String) : TrashEvent()

    object DeleteShoppingLists : TrashEvent()

    data class DeleteShoppingList(val uid: String) : TrashEvent()

    object SelectShoppingListsSort : TrashEvent()

    object SelectShoppingListsDisplayCompleted : TrashEvent()

    object SelectShoppingListsDisplayTotal : TrashEvent()

    data class SelectNavigationItem(val route: UiRoute) : TrashEvent()

    data class SortShoppingLists(val sortBy: SortBy) : TrashEvent()

    data class DisplayShoppingListsCompleted(val displayCompleted: DisplayCompleted) : TrashEvent()

    data class DisplayShoppingListsTotal(val displayTotal: DisplayTotal) : TrashEvent()

    object InvertShoppingListsSort : TrashEvent()

    object ShowBackScreen : TrashEvent()

    data class ShowProducts(val uid: String) : TrashEvent()

    object ShowNavigationDrawer : TrashEvent()

    data class ShowShoppingListMenu(val uid: String) : TrashEvent()

    object HideNavigationDrawer : TrashEvent()

    object HideShoppingListMenu : TrashEvent()

    object HideShoppingListsSort : TrashEvent()

    object HideShoppingListsDisplayCompleted : TrashEvent()

    object HideShoppingListsDisplayTotal : TrashEvent()
}