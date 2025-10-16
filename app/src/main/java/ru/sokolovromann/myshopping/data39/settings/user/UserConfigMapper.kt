package ru.sokolovromann.myshopping.data39.settings.user

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.data39.Mapper
import javax.inject.Inject

class UserConfigMapper @Inject constructor() : Mapper<Preferences, UserConfig>() {

    override fun mapEntityTo(entity: Preferences): UserConfig {
        return UserConfig(
            api = mapApiTo(entity)
        )
    }

    override fun mapEntityFrom(model: UserConfig): Preferences {
        return mutablePreferencesOf().apply {
            val api = mapApiFrom(model.api)
            plusAssign(api)
        }
    }

    fun mapApiTo(entity: Preferences): String? {
        return entity[UserConfigScheme.API]
    }

    fun mapApiFrom(model: String?): Preferences {
        return preferencesOf(
            UserConfigScheme.API to model.orEmpty()
        )
    }
}