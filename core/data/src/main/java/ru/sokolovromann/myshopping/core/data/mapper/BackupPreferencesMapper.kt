package ru.sokolovromann.myshopping.core.data.mapper

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import jakarta.inject.Inject
import jakarta.inject.Singleton
import ru.sokolovromann.myshopping.core.data.datasource.LocalDataStoreScheme
import ru.sokolovromann.myshopping.core.domain.model.BackupDirectory
import ru.sokolovromann.myshopping.core.domain.model.BackupPreferences

@Singleton
class BackupPreferencesMapper @Inject constructor() : DataStoreMapper<BackupPreferences>() {

    override fun toModel(preferences: Preferences) = BackupPreferences(
        BackupDirectory(preferences[LocalDataStoreScheme.Backup.DIRECTORY].orEmpty())
    )

    override fun toPreferences(model: BackupPreferences) = preferencesOf(
        LocalDataStoreScheme.Backup.DIRECTORY to model.directory.value
    )
}