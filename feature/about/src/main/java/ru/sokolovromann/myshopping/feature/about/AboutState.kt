package ru.sokolovromann.myshopping.feature.about

import ru.sokolovromann.myshopping.core.ui.model.UiText

data class AboutState(
    val isLoading: Boolean = true,
    val apiName: UiText = UiText.FromString("")
)