package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.SortBy

sealed class ProductsEvent {

    object AddProduct : ProductsEvent()

    data class EditProduct(val uid: String) : ProductsEvent()

    object EditShoppingListName : ProductsEvent()

    object EditShoppingListReminder : ProductsEvent()

    data class CopyProductToShoppingList(val uid: String) : ProductsEvent()

    data class MoveProductToShoppingList(val uid: String) : ProductsEvent()

    object DeleteProducts : ProductsEvent()

    data class DeleteProduct(val uid: String) : ProductsEvent()

    object ShareProducts : ProductsEvent()

    object SelectProductsSort : ProductsEvent()

    object SelectProductsDisplayCompleted : ProductsEvent()

    object SelectProductsDisplayTotal : ProductsEvent()

    data class SortProducts(val sortBy: SortBy) : ProductsEvent()

    data class DisplayProductsCompleted(val displayCompleted: DisplayCompleted) : ProductsEvent()

    data class DisplayProductsTotal(val displayTotal: DisplayTotal) : ProductsEvent()

    object InvertProductsSort : ProductsEvent()

    data class CompleteProduct(val uid: String) : ProductsEvent()

    data class ActiveProduct(val uid: String) : ProductsEvent()

    object ShowBackScreen : ProductsEvent()

    data class ShowProductMenu(val uid: String) : ProductsEvent()

    object ShowProductsMenu : ProductsEvent()

    object HideProductMenu : ProductsEvent()

    object HideProductsMenu : ProductsEvent()

    object HideProductsSort : ProductsEvent()

    object HideProductsDisplayCompleted : ProductsEvent()

    object HideProductsDisplayTotal : ProductsEvent()

    object CalculateChange : ProductsEvent()
}