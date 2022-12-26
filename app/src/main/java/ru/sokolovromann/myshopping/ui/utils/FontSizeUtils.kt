package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.data.repository.model.FontSize

fun FontSize.toItemTitle(): Int = when (this) {
    FontSize.TINY -> 14
    FontSize.SMALL -> 16
    FontSize.MEDIUM -> 18
    FontSize.LARGE -> 20
    FontSize.HUGE -> 22
}

fun FontSize.toItemBody(): Int = when (this) {
    FontSize.TINY, FontSize.SMALL -> 14
    FontSize.MEDIUM -> 16
    FontSize.LARGE -> 18
    FontSize.HUGE -> 20
}

fun FontSize.toButton(): Int = when (this) {
    FontSize.TINY, FontSize.SMALL -> 12
    FontSize.MEDIUM -> 14
    FontSize.LARGE -> 16
    FontSize.HUGE -> 18
}

fun FontSize.toTextField(): Int = when (this) {
    FontSize.TINY, FontSize.SMALL -> 14
    FontSize.MEDIUM -> 16
    FontSize.LARGE -> 18
    FontSize.HUGE -> 20
}