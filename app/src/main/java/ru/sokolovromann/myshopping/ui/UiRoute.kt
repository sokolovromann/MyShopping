package ru.sokolovromann.myshopping.ui

sealed class UiRoute(val graph: String) {
    object Purchases : UiRoute(graph = "Purchases")
    object Archive : UiRoute(graph = "Archive")
    object Trash : UiRoute(graph = "Trash")
    object Products : UiRoute(graph = "Products")
    object Autocompletes : UiRoute(graph = "Autocompletes")
    object Settings : UiRoute(graph = "Settings")
}

enum class UiRouteKey(val key: String, val placeholder: String) {
    ShoppingUid(
        key = "shopping-uid",
        placeholder = "{shopping-uid}"
    )
}