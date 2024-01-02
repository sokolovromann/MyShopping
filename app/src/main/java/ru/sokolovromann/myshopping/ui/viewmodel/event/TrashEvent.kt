package ru.sokolovromann.myshopping.ui.viewmodel.event

import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.ui.DrawerScreen

sealed class TrashEvent {

    data class OnClickShoppingList(val uid: String) : TrashEvent()

    object OnClickBack : TrashEvent()

    data class OnMoveShoppingListSelected(val location: ShoppingLocation) : TrashEvent()

    object OnClickDeleteShoppingLists : TrashEvent()

    object OnClickEmptyTrash : TrashEvent()

    data class OnDrawerScreenSelected(val drawerScreen: DrawerScreen) : TrashEvent()

    data class OnSelectDrawerScreen(val display: Boolean) : TrashEvent()

    data class OnAllShoppingListsSelected(val selected: Boolean) : TrashEvent()

    data class OnShoppingListSelected(val selected: Boolean, val uid: String) : TrashEvent()
}