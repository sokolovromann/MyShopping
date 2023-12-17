package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.ui.DrawerScreen

sealed class ArchiveEvent {

    data class OnClickShoppingList(val uid: String) : ArchiveEvent()

    object OnClickBack : ArchiveEvent()

    object OnMoveShoppingListsToPurchases : ArchiveEvent()

    object OnMoveShoppingListsToTrash : ArchiveEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : ArchiveEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : ArchiveEvent()

    data class OnDisplayTotalSelected(val displayTotal: DisplayTotal) : ArchiveEvent()

    data class OnSelectDisplayTotal(val expanded: Boolean) : ArchiveEvent()

    data class OnSortSelected(val sortBy: SortBy) : ArchiveEvent()

    object OnReverseSort : ArchiveEvent()

    data class OnSelectSort(val expanded: Boolean) : ArchiveEvent()

    data class OnShowArchiveMenu(val expanded: Boolean) : ArchiveEvent()

    data class OnAllShoppingListsSelected(val selected: Boolean) : ArchiveEvent()

    data class OnShoppingListSelected(val selected: Boolean, val uid: String) : ArchiveEvent()

    data class OnShowHiddenShoppingLists(val display: Boolean) : ArchiveEvent()

    object OnInvertMultiColumns : ArchiveEvent()
}