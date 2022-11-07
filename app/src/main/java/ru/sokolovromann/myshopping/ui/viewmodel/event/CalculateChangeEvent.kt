package ru.sokolovromann.myshopping.ui.viewmodel.event

import androidx.compose.ui.text.input.TextFieldValue

sealed class CalculateChangeEvent {

    object ShowBackScreen : CalculateChangeEvent()

    data class UserMoneyChanged(val value: TextFieldValue) : CalculateChangeEvent()
}