package ru.sokolovromann.myshopping.data.repository.model

data class EditCurrencySymbol(
    val currency: Currency = Currency(),
    val preferences: SettingsPreferences = SettingsPreferences()
)