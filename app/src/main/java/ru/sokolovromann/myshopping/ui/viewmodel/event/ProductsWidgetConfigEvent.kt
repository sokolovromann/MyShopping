package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class ProductsWidgetConfigEvent {

    data class OnCreate(val widgetId: Int?) : ProductsWidgetConfigEvent()

    data class SelectShoppingList(val uid: String) : ProductsWidgetConfigEvent()

    object CancelSelectingShoppingList : ProductsWidgetConfigEvent()
}
