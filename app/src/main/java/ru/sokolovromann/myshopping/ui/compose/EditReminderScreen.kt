package ru.sokolovromann.myshopping.ui.compose

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
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
import ru.sokolovromann.myshopping.ui.compose.event.EditReminderScreenEvent
import ru.sokolovromann.myshopping.ui.navigate
import ru.sokolovromann.myshopping.ui.utils.toItemBody
import ru.sokolovromann.myshopping.ui.viewmodel.EditReminderViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditReminderEvent

@Composable
fun EditReminderScreen(
    navController: NavController,
    viewModel: EditReminderViewModel = hiltViewModel()
) {
    val screenData = viewModel.editReminderState.screenData

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                EditReminderScreenEvent.ShowBackScreen -> navController.popBackStack()

                is EditReminderScreenEvent.ShowPermissions -> {
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
        onDismissRequest = { viewModel.onEvent(EditReminderEvent.CancelSavingReminder) },
        header = { Text(text = screenData.headerText.asCompose()) },
        actionButtons = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                if (screenData.showDeleteButton) {
                    AppDialogActionButton(
                        onClick = { viewModel.onEvent(EditReminderEvent.DeleteReminder) },
                        content = {
                            Text(text = stringResource(R.string.editReminder_action_deleteReminder))
                        }
                    )
                }
                Row {
                    AppDialogActionButton(
                        onClick = { viewModel.onEvent(EditReminderEvent.CancelSavingReminder) },
                        content = {
                            Text(text = stringResource(R.string.editReminder_action_cancelSavingReminder))
                        }
                    )

                    if (screenData.showPermissionError) {
                        AppDialogActionButton(
                            onClick = { viewModel.onEvent(EditReminderEvent.ShowPermissions) },
                            primaryButton = true,
                            content = {
                                Text(text = stringResource(R.string.editReminder_action_showPermissions))
                            }
                        )
                    } else {
                        AppDialogActionButton(
                            onClick = { viewModel.onEvent(EditReminderEvent.SaveReminder) },
                            primaryButton = true,
                            content = {
                                Text(text = stringResource(R.string.editReminder_action_saveReminder))
                            }
                        )
                    }
                }
            }
        },
    ) {
        if (screenData.showPermissionError) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.editReminder_message_permissionError),
                fontSize = screenData.fontSize.toItemBody().sp,
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.error)
            )
        } else {
            Row {
                OutlinedButton(
                    modifier = Modifier.weight(0.5f),
                    onClick = { viewModel.onEvent(EditReminderEvent.SelectReminderDate) },
                    contentPadding = EditReminderContentPadding
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        content = { Text(text = screenData.dateText.asCompose()) }
                    )
                }

                Spacer(modifier = Modifier.size(EditReminderSpacerMediumSize))

                OutlinedButton(
                    modifier = Modifier.weight(0.5f),
                    onClick = { viewModel.onEvent(EditReminderEvent.SelectReminderTime) },
                    contentPadding = EditReminderContentPadding
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        content = { Text(text = screenData.timeText.asCompose()) }
                    )
                }
            }
        }

        if (screenData.showDateDialog) {
            AppDatePickerDialog(
                onDismissRequest = {
                    viewModel.onEvent(EditReminderEvent.CancelSelectingReminderDate)
                },
                onDateChanged = { year: Int, month: Int, dayOfMonth: Int ->
                    val event = EditReminderEvent.ReminderDateChanged(year, month, dayOfMonth)
                    viewModel.onEvent(event)
                },
                dialogStyle = screenData.dateTimeDialogStyle(),
                year = screenData.dateYear,
                monthOfYear = screenData.dateMonth,
                dayOfMonth = screenData.dateDayOfMonth
            )
        }

        if (screenData.showTimeDialog) {
            AppTimePickerDialog(
                onDismissRequest = {
                    viewModel.onEvent(EditReminderEvent.CancelSelectingReminderTime)
                },
                onTimeChanged = { hourOfDay: Int, minute: Int ->
                    val event = EditReminderEvent.ReminderTimeChanged(hourOfDay, minute)
                    viewModel.onEvent(event)
                },
                dialogStyle = screenData.dateTimeDialogStyle(),
                hourOfDay = screenData.timeHourOfDay,
                minute = screenData.timeMinute,
                is24HourFormat = screenData.isTime24HourFormat()
            )
        }
    }
}

private val EditReminderContentPadding = PaddingValues(
    horizontal = 8.dp
)
private val EditReminderSpacerMediumSize = 8.dp