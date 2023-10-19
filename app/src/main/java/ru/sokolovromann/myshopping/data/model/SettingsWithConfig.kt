package ru.sokolovromann.myshopping.data.model

data class SettingsWithConfig(
    val settings: Settings = Settings(),
    val appConfig: AppConfig = AppConfig()
)