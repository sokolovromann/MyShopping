package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences

abstract class DataStoreMapper<M> {

    abstract fun toModel(preferences: Preferences): M

    abstract fun toPreferences(model: M): Preferences
}