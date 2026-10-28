package ru.sokolovromann.myshopping.feature.settings.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val action = BackupSettingsAction.OnImportBackupClicked(it)
            onAction(action)
        }
    }

    SettingsCard(stringResource(R.string.settings_text_backup)) {
        SettingsCardItem(
            title = { Text(stringResource(R.string.settings_text_backup_export)) },
            body = { Text(stringResource(R.string.settings_text_backup_export_description)) },
            onClick = {
                val action = BackupSettingsAction.OnExportBackupClicked
                onAction(action)
            }
        )
        SettingsCardItem(
            title = { Text(stringResource(R.string.settings_text_backup_import)) },
            body = { Text(stringResource(R.string.settings_text_backup_import_description)) },
            onClick = { filePickerLauncher.launch("*/*") }
        )
        SettingsCardItem(
            title = { Text(stringResource(R.string.settings_text_backup_directory)) },
            body = { Text(items.directory.text.asCompose()) },
            onClick = {}
        )
    }
}