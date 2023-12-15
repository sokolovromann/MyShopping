package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.ui.model.UiString

fun StringBuilder.toUiString(): UiString {
    return UiString.FromString(toString())
}