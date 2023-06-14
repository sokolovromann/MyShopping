package ru.sokolovromann.myshopping.ui.compose.event

sealed class ProductsWidgetConfigScreenEvent {

    data class UpdateWidget(val widgetId: Int, val shoppingUid: String) : ProductsWidgetConfigScreenEvent()

    object FinishApp : ProductsWidgetConfigScreenEvent()
}
