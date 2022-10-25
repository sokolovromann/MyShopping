package ru.sokolovromann.myshopping.data.repository.model

data class Settings(
    val settingsValues: SettingsValues = SettingsValues(),
    val preferences: SettingsPreferences = SettingsPreferences()
)