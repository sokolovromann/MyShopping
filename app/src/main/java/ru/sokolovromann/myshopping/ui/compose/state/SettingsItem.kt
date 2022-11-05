package ru.sokolovromann.myshopping.ui.compose.state

data class SettingsItem(
    val title: TextData = TextData(),
    val body: TextData = TextData(),
    val checked: SwitchData = SwitchData()
)