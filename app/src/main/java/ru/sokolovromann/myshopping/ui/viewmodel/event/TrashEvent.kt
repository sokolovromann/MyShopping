package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.ui.UiRoute

sealed class TrashEvent {

    data class MoveShoppingListToPurchases(val uid: String) : TrashEvent()

    data class MoveShoppingListToArchive(val uid: String) : TrashEvent()

    object DeleteShoppingLists : TrashEvent()

    data class DeleteShoppingList(val uid: String) : TrashEvent()

    object SelectDisplayPurchasesTotal : TrashEvent()

    data class SelectNavigationItem(val route: UiRoute) : TrashEvent()

    data class DisplayPurchasesTotal(val displayTotal: DisplayTotal) : TrashEvent()

    object ShowBackScreen : TrashEvent()

    data class ShowProducts(val uid: String) : TrashEvent()

    object ShowNavigationDrawer : TrashEvent()

    data class ShowShoppingListMenu(val uid: String) : TrashEvent()

    object HideNavigationDrawer : TrashEvent()

    object HideShoppingListMenu : TrashEvent()

    object HideDisplayPurchasesTotal : TrashEvent()
}