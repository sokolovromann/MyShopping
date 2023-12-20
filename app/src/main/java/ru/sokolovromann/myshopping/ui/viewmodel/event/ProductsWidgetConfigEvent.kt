package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class ProductsWidgetConfigEvent {

    object OnClickCancel : ProductsWidgetConfigEvent()

    data class OnCreate(val widgetId: Int?) : ProductsWidgetConfigEvent()

    data class OnShoppingListSelected(val uid: String) : ProductsWidgetConfigEvent()
}
