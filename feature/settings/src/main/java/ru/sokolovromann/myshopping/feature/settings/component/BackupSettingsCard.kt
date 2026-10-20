package ru.sokolovromann.myshopping.feature.settings.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.action.BackupSettingsAction
import ru.sokolovromann.myshopping.feature.settings.model.BackupSettingsItems

@Composable
fun BackupSettingsCard(
    onAction: (BackupSettingsAction) -> Unit,
    items: BackupSettingsItems
) {
    SettingsCard(stringResource(R.string.settings_text_backup)) {
        SettingsCardItem(
            title = { Text(stringResource(R.string.settings_text_backup_export)) },
            body = {},
            onClick = {
                val action = BackupSettingsAction.OnExportBackupClicked
                onAction(action)
            }
        )
        SettingsCardItem(
            title = { Text(stringResource(R.string.settings_text_backup_import)) },
            body = {},
            onClick = {}
        )
        SettingsCardItem(
            title = { Text(stringResource(R.string.settings_text_backup_directory)) },
            body = { Text(items.directory.text.asCompose()) },
            onClick = {}
        )
    }
}