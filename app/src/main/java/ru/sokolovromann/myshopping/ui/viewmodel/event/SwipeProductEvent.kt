package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.SwipeProduct

sealed class SwipeProductEvent {

    object OnClickSave : SwipeProductEvent()

    object OnClickCancel : SwipeProductEvent()

    data class OnSelectSwipeProductLeft(val expanded: Boolean) : SwipeProductEvent()

    data class OnSwipeProductLeftSelected(val swipeProduct: SwipeProduct) : SwipeProductEvent()

    data class OnSelectSwipeProductRight(val expanded: Boolean) : SwipeProductEvent()

    data class OnSwipeProductRightSelected(val swipeProduct: SwipeProduct) : SwipeProductEvent()
}