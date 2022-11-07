package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class TrashEvent {

    data class MoveShoppingListToPurchases(val uid: String) : TrashEvent()

    data class MoveShoppingListToArchive(val uid: String) : TrashEvent()

    object DeleteShoppingLists : TrashEvent()

    data class DeleteShoppingList(val uid: String) : TrashEvent()

    object SelectShoppingListsSort : TrashEvent()

    object SelectShoppingListsDisplayCompleted : TrashEvent()

    object SelectShoppingListsDisplayTotal : TrashEvent()

    object SortShoppingListsByCreated : TrashEvent()

    object SortShoppingListsByLastModified : TrashEvent()

    object SortShoppingListsByName : TrashEvent()

    object SortShoppingListsByTotal : TrashEvent()

    object DisplayShoppingListsCompletedFirst : TrashEvent()

    object DisplayShoppingListsCompletedLast : TrashEvent()

    object DisplayShoppingListsAllTotal : TrashEvent()

    object DisplayShoppingListsCompletedTotal : TrashEvent()

    object DisplayShoppingListsActiveTotal : TrashEvent()

    object InvertShoppingListsSort : TrashEvent()

    object ShowBackScreen : TrashEvent()

    data class ShowProducts(val uid: String) : TrashEvent()

    object ShowNavigationDrawer : TrashEvent()

    data class ShowShoppingListMenu(val uid: String) : TrashEvent()

    object HideShoppingListsCompleted : TrashEvent()

    object HideNavigationDrawer : TrashEvent()

    object HideShoppingListMenu : TrashEvent()

    object HideShoppingListsSort : TrashEvent()

    object HideShoppingListsDisplayCompleted : TrashEvent()

    object HideShoppingListsDisplayTotal : TrashEvent()
}