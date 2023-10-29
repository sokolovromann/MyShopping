package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.ShoppingLocation

sealed class MoveProductEvent {

    object AddShoppingList : MoveProductEvent()

    data class MoveProduct(val uid: String) : MoveProductEvent()

    object SelectShoppingListLocation : MoveProductEvent()

    object DisplayHiddenShoppingLists : MoveProductEvent()

    data class ShowShoppingLists(val location: ShoppingLocation) : MoveProductEvent()

    object CancelMovingProduct : MoveProductEvent()

    object HideShoppingListsLocation : MoveProductEvent()
}