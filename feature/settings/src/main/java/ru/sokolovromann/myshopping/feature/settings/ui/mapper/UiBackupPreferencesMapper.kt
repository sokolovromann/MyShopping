package ru.sokolovromann.myshopping.feature.settings.ui.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.BackupPreferences
import ru.sokolovromann.myshopping.core.ui.extension.toUiText
import ru.sokolovromann.myshopping.feature.settings.ui.model.UiBackupPreferences
import ru.sokolovromann.myshopping.feature.settings.ui.model.UiPreference

class UiBackupPreferencesMapper @Inject constructor() {

    fun toPreferences(preferences: BackupPreferences): UiBackupPreferences {
        val directory = UiPreference(
            selected = preferences.directory,
            text = preferences.directory.value.toUiText()
        )
        return UiBackupPreferences(directory)
    }
}