package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.ui.model.UiString

fun String.toUiString(): UiString {
    return UiString.FromString(this)
}