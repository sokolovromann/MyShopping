package ru.sokolovromann.myshopping.data.repository.model

data class SettingsPreferences(
    val fontSize: FontSize = FontSize.DefaultValue,
    val screenSize: ScreenSize = ScreenSize.DefaultValue
)