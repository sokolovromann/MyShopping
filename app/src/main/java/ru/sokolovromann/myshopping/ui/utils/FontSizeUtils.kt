package ru.sokolovromann.myshopping.ui.utils

import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.ui.compose.state.UiText

fun FontSize.toSettingsText(): UiText = when (this) {
    FontSize.SMALL -> UiText.FromResources(R.string.settings_action_selectSmallFontSize)
    FontSize.MEDIUM -> UiText.FromResources(R.string.settings_action_selectMediumFontSize)
    FontSize.LARGE -> UiText.FromResources(R.string.settings_action_selectLargeFontSize)
    FontSize.HUGE -> UiText.FromResources(R.string.settings_action_selectHugeFontSize)
    FontSize.HUGE_2 -> UiText.FromResources(R.string.settings_action_selectHuge2FontSize)
    FontSize.HUGE_3 -> UiText.FromResources(R.string.settings_action_selectHuge3FontSize)
}

fun FontSize.toItemTitle(): Int = when (this) {
    FontSize.SMALL -> 14
    FontSize.MEDIUM -> 16
    FontSize.LARGE -> 18
    FontSize.HUGE -> 20
    FontSize.HUGE_2 -> 22
    FontSize.HUGE_3 -> 24
}

fun FontSize.toItemBody(): Int = when (this) {
    FontSize.SMALL -> 12
    FontSize.MEDIUM -> 14
    FontSize.LARGE -> 16
    FontSize.HUGE -> 18
    FontSize.HUGE_2 -> 20
    FontSize.HUGE_3 -> 22
}

fun FontSize.toButton(): Int = when (this) {
    FontSize.SMALL -> 12
    FontSize.MEDIUM -> 14
    FontSize.LARGE -> 16
    FontSize.HUGE -> 18
    FontSize.HUGE_2 -> 20
    FontSize.HUGE_3 -> 22
}

fun FontSize.toTextField(): Int = when (this) {
    FontSize.SMALL -> 14
    FontSize.MEDIUM -> 16
    FontSize.LARGE -> 18
    FontSize.HUGE -> 20
    FontSize.HUGE_2 -> 22
    FontSize.HUGE_3 -> 24
}

fun FontSize.toHeader6(): Int = when (this) {
    FontSize.SMALL -> 18
    FontSize.MEDIUM -> 20
    FontSize.LARGE -> 22
    FontSize.HUGE -> 24
    FontSize.HUGE_2 -> 26
    FontSize.HUGE_3 -> 28
}

fun FontSize.toWidgetTitle(): Int = when (this) {
    FontSize.SMALL -> 16
    FontSize.MEDIUM -> 18
    FontSize.LARGE -> 20
    FontSize.HUGE -> 22
    FontSize.HUGE_2 -> 24
    FontSize.HUGE_3 -> 26
}

fun FontSize.toWidgetBody(): Int = when (this) {
    FontSize.SMALL -> 14
    FontSize.MEDIUM -> 16
    FontSize.LARGE -> 18
    FontSize.HUGE -> 20
    FontSize.HUGE_2 -> 22
    FontSize.HUGE_3 -> 24
}