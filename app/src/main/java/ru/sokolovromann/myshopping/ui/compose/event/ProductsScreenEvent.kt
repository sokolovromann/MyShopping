package ru.sokolovromann.myshopping.ui.compose.event

sealed class ProductsScreenEvent {

    data class AddProduct(val uid: String) : ProductsScreenEvent()

    data class EditProduct(val shoppingUid: String, val productUid: String) : ProductsScreenEvent()

    data class EditShoppingListName(val uid: String) : ProductsScreenEvent()

    data class EditShoppingListReminder(val uid: String) : ProductsScreenEvent()

    data class EditShoppingListTotal(val uid: String) : ProductsScreenEvent()

    data class CopyProductToShoppingList(val uids: String) : ProductsScreenEvent()

    data class MoveProductToShoppingList(val uids: String) : ProductsScreenEvent()

    object ShowBackScreen : ProductsScreenEvent()

    data class CalculateChange(val uid: String) : ProductsScreenEvent()

    data class ShareProducts(val products: String) : ProductsScreenEvent()

    data class UpdateProductsWidget(val shoppingUid: String) : ProductsScreenEvent()
}