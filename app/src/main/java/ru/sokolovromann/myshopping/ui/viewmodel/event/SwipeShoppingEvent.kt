package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.SwipeShopping

sealed class SwipeShoppingEvent {

    object OnClickSave : SwipeShoppingEvent()

    object OnClickCancel : SwipeShoppingEvent()

    data class OnSelectSwipeShoppingLeft(val expanded: Boolean) : SwipeShoppingEvent()

    data class OnSwipeShoppingLeftSelected(val swipeShopping: SwipeShopping) : SwipeShoppingEvent()

    data class OnSelectSwipeShoppingRight(val expanded: Boolean) : SwipeShoppingEvent()

    data class OnSwipeShoppingRightSelected(val swipeShopping: SwipeShopping) : SwipeShoppingEvent()
}