package ru.sokolovromann.myshopping.feature.settings.ui.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.BackupDirectory
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class UiBackupPreferences(
    val directory: UiPreference<BackupDirectory> = UiPreference(UserPreferencesDefaults.Backup.directory)
)