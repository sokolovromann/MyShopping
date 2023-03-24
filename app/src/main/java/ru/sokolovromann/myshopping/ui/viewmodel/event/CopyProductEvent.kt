package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListLocation

sealed class CopyProductEvent {

    data class CopyProduct(val uid: String) : CopyProductEvent()

    object SelectShoppingListLocation : CopyProductEvent()

    object DisplayHiddenShoppingLists : CopyProductEvent()

    data class ShowShoppingLists(val location: ShoppingListLocation) : CopyProductEvent()

    object CancelCopingProduct : CopyProductEvent()

    object HideShoppingListsLocation : CopyProductEvent()
}