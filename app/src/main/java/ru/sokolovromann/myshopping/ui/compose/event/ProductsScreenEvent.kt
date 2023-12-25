package ru.sokolovromann.myshopping.ui.compose.event

sealed class ProductsScreenEvent {

    object OnShowBackScreen : ProductsScreenEvent()

    data class OnShowAddProduct(val shoppingUid: String) : ProductsScreenEvent()

    data class OnShowEditProduct(val shoppingUid: String, val productUid: String) : ProductsScreenEvent()

    data class OnShowEditName(val shoppingUid: String) : ProductsScreenEvent()

    data class OnShowEditReminder(val shoppingUid: String) : ProductsScreenEvent()

    data class OnShowEditTotal(val shoppingUid: String) : ProductsScreenEvent()

    data class OnShowCopyProduct(val productUids: String) : ProductsScreenEvent()

    data class OnShowMoveProduct(val productUids: String) : ProductsScreenEvent()

    data class OnShowCalculateChange(val shoppingUid: String) : ProductsScreenEvent()

    data class OnShareProducts(val products: String) : ProductsScreenEvent()

    data class OnUpdateProductsWidget(val shoppingUid: String) : ProductsScreenEvent()
}