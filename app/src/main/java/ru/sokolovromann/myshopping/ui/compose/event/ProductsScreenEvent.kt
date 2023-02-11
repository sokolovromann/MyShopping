package ru.sokolovromann.myshopping.ui.compose.event

sealed class ProductsScreenEvent {

    data class AddProduct(val uid: String) : ProductsScreenEvent()

    data class EditProduct(val shoppingUid: String, val productUid: String) : ProductsScreenEvent()

    data class EditShoppingListName(val uid: String) : ProductsScreenEvent()

    data class EditShoppingListReminder(val uid: String) : ProductsScreenEvent()

    data class CopyProductToShoppingList(val uid: String) : ProductsScreenEvent()

    data class MoveProductToShoppingList(val uid: String) : ProductsScreenEvent()

    object ShowBackScreen : ProductsScreenEvent()

    data class CalculateChange(val uid: String) : ProductsScreenEvent()

    data class ShareProducts(val products: String) : ProductsScreenEvent()
}