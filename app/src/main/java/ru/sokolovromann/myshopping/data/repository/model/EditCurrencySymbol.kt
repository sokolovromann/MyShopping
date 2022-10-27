package ru.sokolovromann.myshopping.data.repository.model

data class EditCurrencySymbol(
    val currency: String = "",
    val preferences: SettingsPreferences = SettingsPreferences()
)