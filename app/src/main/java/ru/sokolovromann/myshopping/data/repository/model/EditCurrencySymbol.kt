package ru.sokolovromann.myshopping.data.repository.model

data class EditCurrencySymbol(
    val preferences: AppPreferences = AppPreferences(),
    val appConfig: AppConfig = AppConfig()
)