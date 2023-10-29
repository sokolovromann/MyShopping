package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.ShoppingLocation

sealed class CopyProductEvent {

    object AddShoppingList : CopyProductEvent()

    data class CopyProduct(val uid: String) : CopyProductEvent()

    object SelectShoppingListLocation : CopyProductEvent()

    object DisplayHiddenShoppingLists : CopyProductEvent()

    data class ShowShoppingLists(val location: ShoppingLocation) : CopyProductEvent()

    object CancelCopingProduct : CopyProductEvent()

    object HideShoppingListsLocation : CopyProductEvent()
}