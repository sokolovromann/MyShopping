package ru.sokolovromann.myshopping.data.repository.model

data class EditTaxRate(
    val preferences: AppPreferences = AppPreferences(),
    val appConfig: AppConfig = AppConfig()
)