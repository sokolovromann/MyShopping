package ru.sokolovromann.myshopping.data.model

data class SettingsWithConfig(
    @Deprecated("") val settings: Settings = Settings(),
    val appConfig: AppConfig = AppConfig()
)