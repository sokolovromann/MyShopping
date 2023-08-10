package ru.sokolovromann.myshopping.data.repository.model

data class AppConfig(
    val deviceConfig: DeviceConfig = DeviceConfig(),
    val appBuildConfig: AppBuildConfig = AppBuildConfig(),
    val userPreferences: UserPreferences = UserPreferences()
)