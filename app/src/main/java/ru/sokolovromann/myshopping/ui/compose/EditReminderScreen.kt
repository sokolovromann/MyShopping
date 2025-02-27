package ru.sokolovromann.myshopping.ui.compose

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
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
import ru.sokolovromann.myshopping.data.utils.getHourMinute
import ru.sokolovromann.myshopping.data.utils.getYearMonthDay
import ru.sokolovromann.myshopping.ui.compose.event.EditReminderScreenEvent
import ru.sokolovromann.myshopping.ui.navigate
import ru.sokolovromann.myshopping.ui.utils.getDisplayDate
import ru.sokolovromann.myshopping.ui.utils.getDisplayTime
import ru.sokolovromann.myshopping.ui.utils.isTime24HourFormat
import ru.sokolovromann.myshopping.ui.viewmodel.EditReminderViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditReminderEvent

@Composable
fun EditReminderScreen(
    navController: NavController,
    viewModel: EditReminderViewModel = hiltViewModel()
) {
    val state = viewModel.editReminderState

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                EditReminderScreenEvent.OnShowBackScreen -> navController.popBackStack()

                is EditReminderScreenEvent.OnShowPermissionsScreen -> {
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

    DefaultDialog(
        onDismissRequest = {
            val event = EditReminderEvent.OnClickCancel
            viewModel.onEvent(event)
        },
        header = { Text(stringResource(R.string.editReminder_header)) },
        actionButtons = {
            if (state.displayPermissionError) {
                TextButton(
                    onClick = { viewModel.onEvent(EditReminderEvent.OnClickOpenPermissions) },
                    enabled = !state.waiting,
                    content = { Text(stringResource(R.string.editReminder_action_openPermissions)) }
                )
            } else {
                TextButton(
                    onClick = { viewModel.onEvent(EditReminderEvent.OnClickCancel) },
                    enabled = !state.waiting,
                    content = { Text(stringResource(R.string.editReminder_action_cancelSavingReminder)) }
                )
                TextButton(
                    onClick = { viewModel.onEvent(EditReminderEvent.OnClickSave) },
                    enabled = !state.waiting,
                    content = { Text(stringResource(R.string.editReminder_action_saveReminder)) }
                )
            }
        }
    ) {
        if (state.displayPermissionError) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.editReminder_message_permissionError),
                style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.error)
            )
        } else {
            Row {
                OutlinedButton(
                    modifier = Modifier.weight(0.5f),
                    onClick = { viewModel.onEvent(EditReminderEvent.OnSelectDate(true)) },
                    contentPadding = EditReminderContentPadding
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        content = { Text(text = state.calendar.getDisplayDate().asCompose()) }
                    )
                }

                Spacer(modifier = Modifier.size(EditReminderSpacerMediumSize))

                OutlinedButton(
                    modifier = Modifier.weight(0.5f),
                    onClick = { viewModel.onEvent(EditReminderEvent.OnSelectTime(true)) },
                    contentPadding = EditReminderContentPadding
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        content = { Text(text = state.calendar.getDisplayTime().asCompose()) }
                    )
                }
            }
            if (state.displayDeleteButton) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.onEvent(EditReminderEvent.OnClickDelete) },
                    enabled = !state.waiting,
                    contentPadding = EditReminderContentPadding,
                    content = { Text(stringResource(R.string.editReminder_action_deleteReminder)) }
                )
            }
        }

        if (state.displayDateDialog) {
            DefaultDatePickerDialog(
                onDismissRequest = {
                    viewModel.onEvent(EditReminderEvent.OnSelectDate(false))
                },
                onDateChanged = { year: Int, month: Int, dayOfMonth: Int ->
                    val event = EditReminderEvent.OnDateChanged(year, month, dayOfMonth)
                    viewModel.onEvent(event)
                },
                dialogStyle = state.getDialogStyle(),
                year = state.calendar.getYearMonthDay().first,
                monthOfYear = state.calendar.getYearMonthDay().second,
                dayOfMonth = state.calendar.getYearMonthDay().third
            )
        }

        if (state.displayTimeDialog) {
            DefaultTimePickerDialog(
                onDismissRequest = {
                    viewModel.onEvent(EditReminderEvent.OnSelectTime(false))
                },
                onTimeChanged = { hourOfDay: Int, minute: Int ->
                    val event = EditReminderEvent.OnTimeChanged(hourOfDay, minute)
                    viewModel.onEvent(event)
                },
                dialogStyle = state.getDialogStyle(),
                hourOfDay = state.calendar.getHourMinute().first,
                minute = state.calendar.getHourMinute().second,
                is24HourFormat = state.calendar.isTime24HourFormat()
            )
        }
    }
}

private val EditReminderContentPadding = PaddingValues(
    horizontal = 8.dp
)
private val EditReminderSpacerMediumSize = 8.dp