package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.SortBy

sealed class ProductsEvent {

    object AddProduct : ProductsEvent()

    data class EditProduct(val uid: String) : ProductsEvent()

    object EditShoppingListName : ProductsEvent()

    object EditShoppingListReminder : ProductsEvent()

    data class CopyProductToShoppingList(val uid: String) : ProductsEvent()

    data class MoveProductToShoppingList(val uid: String) : ProductsEvent()

    object MoveShoppingListToPurchases : ProductsEvent()

    object MoveShoppingListToArchive : ProductsEvent()

    object MoveShoppingListToTrash : ProductsEvent()

    data class MoveProductUp(val uid: String) : ProductsEvent()

    data class MoveProductDown(val uid: String) : ProductsEvent()

    object HideProducts : ProductsEvent()

    data class HideProduct(val uid: String) : ProductsEvent()

    object ShareProducts : ProductsEvent()

    object SelectProductsSort : ProductsEvent()

    object SelectDisplayPurchasesTotal : ProductsEvent()

    data class SortProducts(val sortBy: SortBy) : ProductsEvent()

    data class DisplayPurchasesTotal(val displayTotal: DisplayTotal) : ProductsEvent()

    data class CompleteProduct(val uid: String) : ProductsEvent()

    data class ActiveProduct(val uid: String) : ProductsEvent()

    object ShowBackScreen : ProductsEvent()

    data class ShowProductMenu(val uid: String) : ProductsEvent()

    object ShowProductsMenu : ProductsEvent()

    object HideProductMenu : ProductsEvent()

    object HideProductsMenu : ProductsEvent()

    object HideProductsSort : ProductsEvent()

    object HideDisplayPurchasesTotal : ProductsEvent()

    object CalculateChange : ProductsEvent()
}