package ru.sokolovromann.myshopping.ui.compose

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.BackupScreenEvent
import ru.sokolovromann.myshopping.ui.navigate
import ru.sokolovromann.myshopping.ui.viewmodel.BackupViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.BackupEvent

@Composable
fun BackupScreen(
    navController: NavController,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val state = viewModel.backupState
    val registerForActivity = registerSelectFileForResult { uri ->
        val event = BackupEvent.OnFileSelected(uri)
        viewModel.onEvent(event)
    }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                BackupScreenEvent.OnShowBackScreen -> navController.popBackStack()

                is BackupScreenEvent.OnShowPermissionsScreen -> {
                    navController.popBackStack()
                    navController.navigate(
                        intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            val uri = Uri.fromParts("package", it.packageName, null)
                            data = uri
                        }
                    )
                }

                BackupScreenEvent.OnSelectFile -> {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("text/plain"))
                    }

                    registerForActivity.launch(intent)
                }
            }
        }
    }

    DefaultDialog(
        onDismissRequest = {
            val event = BackupEvent.OnClickCancel
            viewModel.onEvent(event)
        },
        header = { Text(stringResource(R.string.backup_header)) },
        actionButtons = {
            if (state.permissionError) {
                TextButton(
                    onClick = { viewModel.onEvent(BackupEvent.OnClickOpenPermissions) },
                    enabled = !state.waiting,
                    content = { Text(stringResource(R.string.backup_action_openPermissions)) }
                )
            } else {
                if (state.displayBack) {
                    TextButton(
                        onClick = { viewModel.onEvent(BackupEvent.OnClickCancel) },
                        enabled = !state.waiting,
                        content = { Text(stringResource(R.string.backup_action_closeBackup)) }
                    )
                } else {
                    TextButton(
                        onClick = { viewModel.onEvent(BackupEvent.OnClickExport) },
                        enabled = !state.waiting,
                        content = { Text(stringResource(R.string.backup_action_export)) }
                    )
                    TextButton(
                        onClick = { viewModel.onEvent(BackupEvent.OnClickImport) },
                        enabled = !state.waiting,
                        content = { Text(stringResource(R.string.backup_action_import)) }
                    )
                }
            }
        }
    ) {
        if (state.permissionError) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.backup_message_permissionError),
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.error)
            )
        } else {
            if (state.waiting) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    content = { CircularProgressIndicator() }
                )
            } else {
                if (state.messageText.isNotEmpty()) {
                    Spacer(modifier = Modifier.size(BackupSpacerMediumSize))
                    Text(
                        text = state.messageText.asCompose(),
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.onSurface
                        )
                    )
                }

                if (state.locationText.isNotEmpty()) {
                    Text(
                        text = state.locationText.asCompose(),
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                        )
                    )
                }

                if (state.warningText.isNotEmpty()) {
                    Spacer(modifier = Modifier.size(BackupSpacerMediumSize))
                    Text(
                        text = state.warningText.asCompose(),
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.onSurface
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun registerSelectFileForResult(
    onResult: (Uri) -> Unit
) = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        result.data?.data?.let { onResult(it) }
    }
}

private val BackupSpacerMediumSize = 8.dp