package ru.sokolovromann.myshopping.ui.compose

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.BackupScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.ScreenState
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.navigate
import ru.sokolovromann.myshopping.ui.utils.toButton
import ru.sokolovromann.myshopping.ui.utils.toItemBody
import ru.sokolovromann.myshopping.ui.utils.toItemTitle
import ru.sokolovromann.myshopping.ui.viewmodel.BackupViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.BackupEvent

@Composable
fun BackupScreen(
    navController: NavController,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val screenData = viewModel.backupState.screenData
    val registerForActivity = registerSelectFileForResult { uri ->
        val event = BackupEvent.Import(uri)
        viewModel.onEvent(event)
    }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                BackupScreenEvent.ShowBackScreen -> navController.popBackStack()

                BackupScreenEvent.ShowSelectFile -> {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("text/plain"))
                    }

                    registerForActivity.launch(intent)
                }

                is BackupScreenEvent.ShowPermissions -> {
                    navController.popBackStack()
                    navController.navigate(
                        intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            val uri = Uri.fromParts("package", it.packageName, null)
                            data = uri
                        }
                    )
                }
            }
        }
    }

    AppDialog(
        onDismissRequest = { viewModel.onEvent(BackupEvent.ShowBackScreen) },
        header = { Text(text = stringResource(R.string.backup_header)) },
        actionButtons = {
            AppDialogActionButton(
                onClick = { viewModel.onEvent(BackupEvent.ShowBackScreen) },
                content = {
                    Text(text = stringResource(R.string.backup_action_closeBackup))
                }
            )

            if (screenData.showPermissionError) {
                AppDialogActionButton(
                    onClick = { viewModel.onEvent(BackupEvent.ShowPermissions) },
                    primaryButton = true,
                    content = {
                        Text(text = stringResource(R.string.backup_action_showPermissions))
                    }
                )
            }
        }
    ) {
        if (screenData.showPermissionError) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.backup_message_permissionError),
                fontSize = screenData.fontSize.toItemBody().sp,
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.error)
            )
        } else {
            if (screenData.screenState == ScreenState.Loading ||
                screenData.screenState == ScreenState.Saving
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    content = { CircularProgressIndicator() }
                )
            } else {
                Row {
                    OutlinedButton(
                        modifier = Modifier.weight(0.5f),
                        onClick = { viewModel.onEvent(BackupEvent.Export) },
                        contentPadding = BackupContentPadding
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = stringResource(R.string.backup_action_export),
                                fontSize = screenData.fontSize.toButton().sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(BackupSpacerMediumSize))

                    OutlinedButton(
                        modifier = Modifier.weight(0.5f),
                        onClick = { viewModel.onEvent(BackupEvent.SelectFile) },
                        contentPadding = BackupContentPadding
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = stringResource(R.string.backup_action_import),
                                fontSize = screenData.fontSize.toButton().sp
                            )
                        }
                    }
                }

                if (screenData.messageText != UiText.Nothing) {
                    Spacer(modifier = Modifier.size(BackupSpacerMediumSize))
                    Text(
                        text = screenData.messageText.asCompose(),
                        fontSize = screenData.fontSize.toItemTitle().sp,
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.onSurface
                        )
                    )
                }

                if (screenData.locationText != UiText.Nothing) {
                    Text(
                        text = screenData.locationText.asCompose(),
                        fontSize = screenData.fontSize.toItemBody().sp,
                        style = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
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

private val BackupContentPadding = PaddingValues(
    horizontal = 8.dp
)
private val BackupSpacerMediumSize = 8.dp