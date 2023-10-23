package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.ui.UiRoute

sealed class TrashEvent {

    object MoveShoppingListsToPurchases : TrashEvent()

    object MoveShoppingListsToArchive : TrashEvent()

    object DeleteShoppingLists : TrashEvent()

    object EmptyTrash : TrashEvent()

    object SelectDisplayPurchasesTotal : TrashEvent()

    data class SelectNavigationItem(val route: UiRoute) : TrashEvent()

    data class SelectShoppingList(val uid: String) : TrashEvent()

    object SelectAllShoppingLists : TrashEvent()

    data class UnselectShoppingList(val uid: String) : TrashEvent()

    object CancelSelectingShoppingLists : TrashEvent()

    data class DisplayPurchasesTotal(val displayTotal: DisplayTotal) : TrashEvent()

    object ShowBackScreen : TrashEvent()

    data class ShowProducts(val uid: String) : TrashEvent()

    object ShowNavigationDrawer : TrashEvent()

    object HideNavigationDrawer : TrashEvent()

    object HideDisplayPurchasesTotal : TrashEvent()
}