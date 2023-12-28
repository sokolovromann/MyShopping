package ru.sokolovromann.myshopping.ui.model

data class SettingItem(
    val uid: SettingUid,
    val title: UiString,
    val body: UiString,
    val checked: Boolean?
)