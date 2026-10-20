package ru.sokolovromann.myshopping.feature.settings.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.BackupPreferences
import ru.sokolovromann.myshopping.core.ui.extension.toUiText
import ru.sokolovromann.myshopping.feature.settings.model.BackupSettingsItems
import ru.sokolovromann.myshopping.feature.settings.model.SettingsItem

class BackupSettingsMapper @Inject constructor() {

    fun toItems(preferences: BackupPreferences): BackupSettingsItems {
        val directory = SettingsItem(
            selected = preferences.directory,
            text = preferences.directory.value.toUiText()
        )
        return BackupSettingsItems(
            directory = directory
        )
    }
}