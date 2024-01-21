package ru.sokolovromann.myshopping.ui.viewmodel.event

sealed class MaxAutocompletesEvent {

    object OnClickSave : MaxAutocompletesEvent()

    object OnClickCancel : MaxAutocompletesEvent()

    object OnClickPlusOneName : MaxAutocompletesEvent()

    object OnClickMinusOneName : MaxAutocompletesEvent()

    object OnClickPlusOneQuantity : MaxAutocompletesEvent()

    object OnClickMinusOneQuantity : MaxAutocompletesEvent()

    object OnClickPlusOneMoney : MaxAutocompletesEvent()

    object OnClickMinusOneMoney : MaxAutocompletesEvent()

    object OnClickPlusOneOther : MaxAutocompletesEvent()

    object OnClickMinusOneOther : MaxAutocompletesEvent()
}