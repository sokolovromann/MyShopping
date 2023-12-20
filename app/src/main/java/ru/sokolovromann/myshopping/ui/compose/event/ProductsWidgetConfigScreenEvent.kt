package ru.sokolovromann.myshopping.ui.compose.event

sealed class ProductsWidgetConfigScreenEvent {

    data class OnUpdate(val widgetId: Int, val shoppingUid: String) : ProductsWidgetConfigScreenEvent()

    object OnFinishApp : ProductsWidgetConfigScreenEvent()
}
