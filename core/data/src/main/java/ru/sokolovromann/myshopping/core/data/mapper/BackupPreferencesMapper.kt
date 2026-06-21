package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import ru.sokolovromann.myshopping.core.data.datasource.BackupPreferencesScheme
import ru.sokolovromann.myshopping.core.domain.model.BackupDirectory
import ru.sokolovromann.myshopping.core.domain.model.BackupPreferences

class BackupPreferencesMapper : DataStoreMapper<BackupPreferences>() {

    override fun toModel(preferences: Preferences) = BackupPreferences(
        BackupDirectory(preferences[BackupPreferencesScheme.DIRECTORY_KEY].orEmpty())
    )

    override fun toPreferences(model: BackupPreferences) = preferencesOf(
        BackupPreferencesScheme.DIRECTORY_KEY to model.directory.value
    )
}