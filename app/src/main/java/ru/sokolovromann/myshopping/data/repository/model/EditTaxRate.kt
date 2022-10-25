package ru.sokolovromann.myshopping.data.repository.model

data class EditTaxRate(
    val taxRate: TaxRate = TaxRate(),
    val preferences: SettingsPreferences = SettingsPreferences()
)