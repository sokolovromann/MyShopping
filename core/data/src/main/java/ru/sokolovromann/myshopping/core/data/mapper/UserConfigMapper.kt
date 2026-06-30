package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.LocalDataStoreScheme
import ru.sokolovromann.myshopping.core.domain.model.API
import ru.sokolovromann.myshopping.core.domain.model.UserConfig

@Singleton
class UserConfigMapper @Inject constructor() : DataStoreMapper<UserConfig>() {

    override fun toModel(preferences: Preferences) = UserConfig(
        preferences[LocalDataStoreScheme.User.API]?.toLongOrNull()?.let { API(it) }
    )

    override fun toPreferences(model: UserConfig) = preferencesOf(
        LocalDataStoreScheme.User.API to model.api?.value?.toString().orEmpty()
    )
}