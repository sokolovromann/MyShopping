package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue

sealed class CalculateChangeEvent {

    object OnClickCancel : CalculateChangeEvent()

    data class OnUserMoneyChanged(val value: TextFieldValue) : CalculateChangeEvent()
}