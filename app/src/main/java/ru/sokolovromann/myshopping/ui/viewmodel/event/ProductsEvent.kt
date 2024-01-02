package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.SortBy

sealed class ProductsEvent {

    data class OnClickProduct(val productUid: String, val completed: Boolean) : ProductsEvent()

    object OnClickAddProduct : ProductsEvent()

    data class OnClickEditProduct(val productUid: String) : ProductsEvent()

    object OnClickBack : ProductsEvent()

    object OnClickEditName : ProductsEvent()

    object OnClickEditReminder : ProductsEvent()

    object OnClickEditTotal : ProductsEvent()

    object OnClickDeleteTotal : ProductsEvent()

    object OnClickPinProducts: ProductsEvent()

    object OnClickCopyProducts : ProductsEvent()

    object OnClickMoveProducts : ProductsEvent()

    object OnClickCopyShoppingList : ProductsEvent()

    data class OnClickMoveProductUp(val productUid: String) : ProductsEvent()

    data class OnClickMoveProductDown(val productUid: String) : ProductsEvent()

    object OnClickDeleteProducts : ProductsEvent()

    object OnClickShareProducts : ProductsEvent()

    object OnClickCalculateChange : ProductsEvent()

    data class OnMoveShoppingListSelected(val location: ShoppingLocation) : ProductsEvent()

    data class OnDisplayTotalSelected(val displayTotal: DisplayTotal) : ProductsEvent()

    data class OnSelectDisplayTotal(val expanded: Boolean) : ProductsEvent()

    data class OnSortSelected(val sortBy: SortBy) : ProductsEvent()

    object OnReverseSort : ProductsEvent()

    data class OnSelectSort(val expanded: Boolean) : ProductsEvent()

    object OnInvertSortFormatted : ProductsEvent()

    data class OnShowProductsMenu(val expanded: Boolean) : ProductsEvent()

    data class OnShowItemMoreMenu(val expanded: Boolean) : ProductsEvent()

    data class OnShowShoppingMenu(val expanded: Boolean) : ProductsEvent()

    data class OnAllProductsSelected(val selected: Boolean) : ProductsEvent()

    data class OnProductSelected(val selected: Boolean, val productUid: String) : ProductsEvent()

    data class OnShowHiddenProducts(val display: Boolean) : ProductsEvent()

    object OnInvertMultiColumns : ProductsEvent()
}