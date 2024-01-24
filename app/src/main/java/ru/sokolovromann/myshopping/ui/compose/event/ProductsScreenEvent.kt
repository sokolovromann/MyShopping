package ru.sokolovromann.myshopping.ui.compose.event

sealed class ProductsScreenEvent {

    object OnShowBackScreen : ProductsScreenEvent()

    data class OnShowAddProductScreen(val shoppingUid: String) : ProductsScreenEvent()

    data class OnShowEditProductScreen(val shoppingUid: String, val productUid: String) : ProductsScreenEvent()

    data class OnShowEditNameScreen(val shoppingUid: String) : ProductsScreenEvent()

    data class OnShowEditReminderScreen(val shoppingUid: String) : ProductsScreenEvent()

    data class OnShowEditTotalScreen(val shoppingUid: String) : ProductsScreenEvent()

    data class OnShowCopyProductScreen(val productUids: String) : ProductsScreenEvent()

    data class OnShowMoveProductScreen(val productUids: String) : ProductsScreenEvent()

    data class OnShowCalculateChangeScreen(val shoppingUid: String) : ProductsScreenEvent()

    data class OnShareProducts(val products: String) : ProductsScreenEvent()

    data class OnUpdateProductsWidget(val shoppingUid: String) : ProductsScreenEvent()

    object OnHideKeyboard : ProductsScreenEvent()
}