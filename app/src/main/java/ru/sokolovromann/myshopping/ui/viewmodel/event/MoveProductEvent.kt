package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class MoveProductEvent {

    data class MoveProduct(val uid: String) : MoveProductEvent()

    object SelectShoppingListsLocation : MoveProductEvent()

    object DisplayShoppingListsPurchases : MoveProductEvent()

    object DisplayShoppingListsArchive : MoveProductEvent()

    object DisplayShoppingListsTrash : MoveProductEvent()

    object ShowBackScreen : MoveProductEvent()

    object HideShoppingListsLocation : MoveProductEvent()
}