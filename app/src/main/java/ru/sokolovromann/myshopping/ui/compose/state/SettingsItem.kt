package ru.sokolovromann.myshopping.ui.compose.state

data class SettingsItem(
    val title: TextData = TextData.Title,
    val body: TextData = TextData.Body,
    val completed: SwitchData = SwitchData.OnSurface
)