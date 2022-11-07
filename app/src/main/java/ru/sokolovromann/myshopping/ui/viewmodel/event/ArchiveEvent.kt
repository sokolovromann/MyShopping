package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class ArchiveEvent {

    data class MoveShoppingListToPurchases(val uid: String) : ArchiveEvent()

    data class MoveShoppingListToTrash(val uid: String) : ArchiveEvent()

    object SelectShoppingListsSort : ArchiveEvent()

    object SelectShoppingListsDisplayCompleted : ArchiveEvent()

    object SelectShoppingListsDisplayTotal : ArchiveEvent()

    object SortShoppingListsByCreated : ArchiveEvent()

    object SortShoppingListsByLastModified : ArchiveEvent()

    object SortShoppingListsByName : ArchiveEvent()

    object SortShoppingListsByTotal : ArchiveEvent()

    object DisplayShoppingListsCompletedFirst : ArchiveEvent()

    object DisplayShoppingListsCompletedLast : ArchiveEvent()

    object DisplayShoppingListsAllTotal : ArchiveEvent()

    object DisplayShoppingListsCompletedTotal : ArchiveEvent()

    object DisplayShoppingListsActiveTotal : ArchiveEvent()

    object InvertShoppingListsSort : ArchiveEvent()

    object ShowBackScreen : ArchiveEvent()

    data class ShowProducts(val uid: String) : ArchiveEvent()

    object ShowNavigationDrawer : ArchiveEvent()

    data class ShowShoppingListMenu(val uid: String) : ArchiveEvent()

    object HideShoppingListsCompleted : ArchiveEvent()

    object HideNavigationDrawer : ArchiveEvent()

    object HideShoppingListMenu : ArchiveEvent()

    object HideShoppingListsSort : ArchiveEvent()

    object HideShoppingListsDisplayCompleted : ArchiveEvent()

    object HideShoppingListsDisplayTotal : ArchiveEvent()
}