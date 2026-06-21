package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.core.data.datasource.UserConfigScheme
import ru.sokolovromann.myshopping.core.domain.model.API
import ru.sokolovromann.myshopping.core.domain.model.UserConfig

class UserConfigMapper : DataStoreMapper<UserConfig>() {

    override fun toModel(preferences: Preferences) = UserConfig(
        preferences[UserConfigScheme.API_KEY]?.toLongOrNull()?.let { API(it) }
    )

    override fun toPreferences(model: UserConfig) = preferencesOf(
        UserConfigScheme.API_KEY to model.api?.value?.toString().orEmpty()
    )
}