package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class CopyProductEvent {

    data class CopyProduct(val uid: String) : CopyProductEvent()

    object SelectShoppingListsLocation : CopyProductEvent()

    object DisplayShoppingListsPurchases : CopyProductEvent()

    object DisplayShoppingListsArchive : CopyProductEvent()

    object DisplayShoppingListsTrash : CopyProductEvent()

    object ShowBackScreen : CopyProductEvent()

    object HideShoppingListsLocation : CopyProductEvent()
}