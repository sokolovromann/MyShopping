package ru.sokolovromann.myshopping.settings.user

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.io.TwoSidedMapper
import javax.inject.Inject

class UserConfigMapper @Inject constructor() : TwoSidedMapper<Preferences, UserConfig>() {

    override fun mapTo(a: Preferences): UserConfig {
        return UserConfig(
            api = a[UserConfigScheme.API]
        )
    }

    override fun mapFrom(b: UserConfig): Preferences {
        return preferencesOf(
            UserConfigScheme.API to b.api.orEmpty()
        )
    }
}