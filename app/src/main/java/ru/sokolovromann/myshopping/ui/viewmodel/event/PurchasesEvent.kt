package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.ui.DrawerScreen

sealed class PurchasesEvent {

    data class OnClickShoppingList(val uid: String) : PurchasesEvent()

    object OnClickAdd : PurchasesEvent()

    object OnClickBack : PurchasesEvent()

    object OnClickMoveToArchive : PurchasesEvent()

    object OnClickMoveToTrash : PurchasesEvent()

    object OnClickPin: PurchasesEvent()

    object OnClickCopy : PurchasesEvent()

    data class OnClickMoveUp(val uid: String) : PurchasesEvent()

    data class OnClickMoveDown(val uid: String) : PurchasesEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : PurchasesEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : PurchasesEvent()

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