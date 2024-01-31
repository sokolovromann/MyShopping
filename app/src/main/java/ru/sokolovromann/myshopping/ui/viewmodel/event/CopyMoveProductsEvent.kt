package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.ShoppingLocation

sealed class CopyMoveProductsEvent {

    object OnClickAddShoppingList : CopyMoveProductsEvent()

    object OnClickCancel : CopyMoveProductsEvent()

    data class OnClickCopyOrMoveProducts(val shoppingUid: String) : CopyMoveProductsEvent()

    data class OnLocationSelected(val location: ShoppingLocation) : CopyMoveProductsEvent()

    data class OnSelectLocation(val expanded: Boolean) : CopyMoveProductsEvent()

    data class OnShowHiddenShoppingLists(val display: Boolean) : CopyMoveProductsEvent()
}