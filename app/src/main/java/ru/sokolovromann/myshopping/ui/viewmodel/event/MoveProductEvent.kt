package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListLocation

sealed class MoveProductEvent {

    data class MoveProduct(val uid: String) : MoveProductEvent()

    object SelectShoppingListLocation : MoveProductEvent()

    object DisplayHiddenShoppingLists : MoveProductEvent()

    data class ShowShoppingLists(val location: ShoppingListLocation) : MoveProductEvent()

    object CancelMovingProduct : MoveProductEvent()

    object HideShoppingListsLocation : MoveProductEvent()
}