package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.ShoppingLocation

sealed class CopyProductEvent {

    object OnClickAddShoppingList : CopyProductEvent()

    object OnClickCancel : CopyProductEvent()

    data class OnClickCopyProducts(val shoppingUid: String) : CopyProductEvent()

    data class OnLocationSelected(val location: ShoppingLocation) : CopyProductEvent()

    data class OnSelectLocation(val expanded: Boolean) : CopyProductEvent()

    data class OnShowHiddenShoppingLists(val display: Boolean) : CopyProductEvent()
}