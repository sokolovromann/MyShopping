package ru.sokolovromann.myshopping.data.local.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AppConfigEntity(
    @Transient val deviceConfig: DeviceConfigEntity = DeviceConfigEntity(),
    val appBuildConfig: AppBuildConfigEntity = AppBuildConfigEntity(),
    val userPreferences: UserPreferencesEntity = UserPreferencesEntity()
)