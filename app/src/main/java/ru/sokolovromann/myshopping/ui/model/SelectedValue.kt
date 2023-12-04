package ru.sokolovromann.myshopping.ui.model

data class SelectedValue<S>(
    val selected: S,
    val text: UiString = UiString.FromString("")
)