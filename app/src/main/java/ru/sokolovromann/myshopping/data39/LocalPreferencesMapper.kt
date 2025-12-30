package ru.sokolovromann.myshopping.data39

import androidx.datastore.preferences.core.Preferences

abstract class LocalPreferencesMapper<D> : LocalDataMapper() {

    abstract fun toPreferences(data: D): Preferences

    abstract fun fromPreferences(preferences: Preferences): D
}