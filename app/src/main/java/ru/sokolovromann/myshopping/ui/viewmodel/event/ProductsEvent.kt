package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.SortBy

sealed class ProductsEvent {

    object AddProduct : ProductsEvent()

    data class EditProduct(val uid: String) : ProductsEvent()

    object EditShoppingListName : ProductsEvent()

    object EditShoppingListReminder : ProductsEvent()

    object CopyProductsToShoppingList : ProductsEvent()

    object MoveProductsToShoppingList : ProductsEvent()

    object MoveShoppingListToPurchases : ProductsEvent()

    object MoveShoppingListToArchive : ProductsEvent()

    object MoveShoppingListToTrash : ProductsEvent()

    data class MoveProductUp(val uid: String) : ProductsEvent()

    data class MoveProductDown(val uid: String) : ProductsEvent()

    object DeleteProducts : ProductsEvent()

    object ShareProducts : ProductsEvent()

    object SelectProductsSort : ProductsEvent()

    object SelectDisplayPurchasesTotal : ProductsEvent()

    data class SelectProduct(val uid: String) : ProductsEvent()

    object SelectAllProducts : ProductsEvent()

    data class UnselectProduct(val uid: String) : ProductsEvent()

    object CancelSelectingProducts : ProductsEvent()

    data class SortProducts(val sortBy: SortBy) : ProductsEvent()

    object ReverseSortProducts : ProductsEvent()

    data class DisplayPurchasesTotal(val displayTotal: DisplayTotal) : ProductsEvent()

    object DisplayHiddenProducts : ProductsEvent()

    data class CompleteProduct(val uid: String) : ProductsEvent()

    data class ActiveProduct(val uid: String) : ProductsEvent()

    object ShowBackScreen : ProductsEvent()

    object ShowProductsMenu : ProductsEvent()

    object HideProductsMenu : ProductsEvent()

    object HideProductsSort : ProductsEvent()

    object HideDisplayPurchasesTotal : ProductsEvent()

    object CalculateChange : ProductsEvent()

    object InvertProductsMultiColumns : ProductsEvent()
}