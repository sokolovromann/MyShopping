package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun FontSize.toSettingsText(): UiText = when (this) {
    FontSize.SMALL -> UiText.FromResources(R.string.settings_action_selectSmallFontSize)
    FontSize.MEDIUM -> UiText.FromResources(R.string.settings_action_selectMediumFontSize)
    FontSize.LARGE -> UiText.FromResources(R.string.settings_action_selectLargeFontSize)
    FontSize.VERY_LARGE -> UiText.FromResources(R.string.settings_action_selectHugeFontSize)
    FontSize.HUGE -> UiText.FromResources(R.string.settings_action_selectHuge2FontSize)
    FontSize.VERY_HUGE -> UiText.FromResources(R.string.settings_action_selectHuge3FontSize)
}

fun FontSize.toItemTitle(): Int = when (this) {
    FontSize.SMALL -> 14
    FontSize.MEDIUM -> 16
    FontSize.LARGE -> 18
    FontSize.VERY_LARGE -> 20
    FontSize.HUGE -> 22
    FontSize.VERY_HUGE -> 24
}

fun FontSize.toItemBody(): Int = when (this) {
    FontSize.SMALL -> 12
    FontSize.MEDIUM -> 14
    FontSize.LARGE -> 16
    FontSize.VERY_LARGE -> 18
    FontSize.HUGE -> 20
    FontSize.VERY_HUGE -> 22
}

fun FontSize.toButton(): Int = when (this) {
    FontSize.SMALL -> 12
    FontSize.MEDIUM -> 14
    FontSize.LARGE -> 16
    FontSize.VERY_LARGE -> 18
    FontSize.HUGE -> 20
    FontSize.VERY_HUGE -> 22
}

fun FontSize.toTextField(): Int = when (this) {
    FontSize.SMALL -> 14
    FontSize.MEDIUM -> 16
    FontSize.LARGE -> 18
    FontSize.VERY_LARGE -> 20
    FontSize.HUGE -> 22
    FontSize.VERY_HUGE -> 24
}

fun FontSize.toHeader6(): Int = when (this) {
    FontSize.SMALL -> 18
    FontSize.MEDIUM -> 20
    FontSize.LARGE -> 22
    FontSize.VERY_LARGE -> 24
    FontSize.HUGE -> 26
    FontSize.VERY_HUGE -> 28
}

fun FontSize.toWidgetTitle(): Int = when (this) {
    FontSize.SMALL -> 16
    FontSize.MEDIUM -> 18
    FontSize.LARGE -> 20
    FontSize.VERY_LARGE -> 22
    FontSize.HUGE -> 24
    FontSize.VERY_HUGE -> 26
}

fun FontSize.toWidgetBody(): Int = when (this) {
    FontSize.SMALL -> 14
    FontSize.MEDIUM -> 16
    FontSize.LARGE -> 18
    FontSize.VERY_LARGE -> 20
    FontSize.HUGE -> 22
    FontSize.VERY_HUGE -> 24
}