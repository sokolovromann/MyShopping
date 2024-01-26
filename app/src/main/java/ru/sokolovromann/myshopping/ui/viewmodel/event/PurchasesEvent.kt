package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.ui.DrawerScreen

sealed class PurchasesEvent {

    data class OnClickShoppingList(val uid: String) : PurchasesEvent()

    object OnClickAddShoppingList : PurchasesEvent()

    object OnClickBack : PurchasesEvent()

    data class OnMoveShoppingListSelected(val location: ShoppingLocation) : PurchasesEvent()

    object OnClickPinShoppingLists: PurchasesEvent()

    object OnClickCopyShoppingLists : PurchasesEvent()

    data class OnClickMoveShoppingListUp(val uid: String) : PurchasesEvent()

    data class OnClickMoveShoppingListDown(val uid: String) : PurchasesEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : PurchasesEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : PurchasesEvent()

    object OnClickSearchShoppingLists : PurchasesEvent()

    data class OnSearchValueChanged(val value: TextFieldValue) : PurchasesEvent()

    object OnInvertSearch : PurchasesEvent()

    data class OnDisplayProductsSelected(val displayProducts: DisplayProducts) : PurchasesEvent()

    data class OnSelectDisplayProducts(val expanded: Boolean) : PurchasesEvent()

    data class OnDisplayTotalSelected(val displayTotal: DisplayTotal) : PurchasesEvent()

    data class OnSelectDisplayTotal(val expanded: Boolean) : PurchasesEvent()

    data class OnSortSelected(val sortBy: SortBy) : PurchasesEvent()

    object OnReverseSort : PurchasesEvent()

    data class OnSelectSort(val expanded: Boolean) : PurchasesEvent()

    data class OnShowPurchasesMenu(val expanded: Boolean) : PurchasesEvent()

    data class OnShowItemMoreMenu(val expanded: Boolean) : PurchasesEvent()

    data class OnAllShoppingListsSelected(val selected: Boolean) : PurchasesEvent()

    data class OnShoppingListSelected(val selected: Boolean, val uid: String) : PurchasesEvent()

    data class OnShowHiddenShoppingLists(val display: Boolean) : PurchasesEvent()

    object OnInvertMultiColumns : PurchasesEvent()
}