package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.ShoppingLocation

sealed class MoveProductEvent {

    object OnClickAdd : MoveProductEvent()

    object OnClickCancel : MoveProductEvent()

    data class OnClickMove(val uid: String) : MoveProductEvent()

    data class OnLocationSelected(val location: ShoppingLocation) : MoveProductEvent()

    data class OnSelectLocation(val expanded: Boolean) : MoveProductEvent()

    data class OnShowHiddenShoppingLists(val display: Boolean) : MoveProductEvent()
}