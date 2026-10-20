package ru.sokolovromann.myshopping.feature.settings.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.BackupDirectory
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class BackupSettingsItems(
    val directory: SettingsItem<BackupDirectory> = SettingsItem(UserPreferencesDefaults.Backup.directory)
)