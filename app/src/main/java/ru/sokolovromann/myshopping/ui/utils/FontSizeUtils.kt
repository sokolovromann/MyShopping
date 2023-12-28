package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.data.model.FontSize

@Deprecated("Use UiFontSize")
fun FontSize.toItemTitle(): Int = when (this) {
    FontSize.SMALL -> 14
    FontSize.MEDIUM -> 16
    FontSize.LARGE -> 18
    FontSize.VERY_LARGE -> 20
    FontSize.HUGE -> 22
    FontSize.VERY_HUGE -> 24
}

@Deprecated("Use UiFontSize")
fun FontSize.toItemBody(): Int = when (this) {
    FontSize.SMALL -> 12
    FontSize.MEDIUM -> 14
    FontSize.LARGE -> 16
    FontSize.VERY_LARGE -> 18
    FontSize.HUGE -> 20
    FontSize.VERY_HUGE -> 22
}

@Deprecated("Use UiFontSize")
fun FontSize.toButton(): Int = when (this) {
    FontSize.SMALL -> 12
    FontSize.MEDIUM -> 14
    FontSize.LARGE -> 16
    FontSize.VERY_LARGE -> 18
    FontSize.HUGE -> 20
    FontSize.VERY_HUGE -> 22
}

@Deprecated("Use UiFontSize")
fun FontSize.toTextField(): Int = when (this) {
    FontSize.SMALL -> 14
    FontSize.MEDIUM -> 16
    FontSize.LARGE -> 18
    FontSize.VERY_LARGE -> 20
    FontSize.HUGE -> 22
    FontSize.VERY_HUGE -> 24
}

@Deprecated("Use UiFontSize")
fun FontSize.toHeader6(): Int = when (this) {
    FontSize.SMALL -> 18
    FontSize.MEDIUM -> 20
    FontSize.LARGE -> 22
    FontSize.VERY_LARGE -> 24
    FontSize.HUGE -> 26
    FontSize.VERY_HUGE -> 28
}

@Deprecated("Use UiFontSize")
fun FontSize.toWidgetTitle(): Int = when (this) {
    FontSize.SMALL -> 16
    FontSize.MEDIUM -> 18
    FontSize.LARGE -> 20
    FontSize.VERY_LARGE -> 22
    FontSize.HUGE -> 24
    FontSize.VERY_HUGE -> 26
}

@Deprecated("Use UiFontSize")
fun FontSize.toWidgetBody(): Int = when (this) {
    FontSize.SMALL -> 14
    FontSize.MEDIUM -> 16
    FontSize.LARGE -> 18
    FontSize.VERY_LARGE -> 20
    FontSize.HUGE -> 22
    FontSize.VERY_HUGE -> 24
}