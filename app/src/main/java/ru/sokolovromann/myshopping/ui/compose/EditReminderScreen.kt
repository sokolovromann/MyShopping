package ru.sokolovromann.myshopping.ui.compose

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.EditReminderScreenEvent
import ru.sokolovromann.myshopping.ui.viewmodel.EditReminderViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditReminderEvent

@Composable
fun EditReminderScreen(
    navController: NavController,
    viewModel: EditReminderViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                EditReminderScreenEvent.ShowBackScreen -> navController.popBackStack()
            }
        }
    }

    AppDialog(
        onDismissRequest = { viewModel.onEvent(EditReminderEvent.CancelSavingReminder) },
        header = { Text(text = viewModel.editReminderState.screenData.headerText.asCompose()) },
        actionButtons = { ActionButtons(viewModel) },
        content = { Content(viewModel) }
    )
}

@Composable
private fun ActionButtons(viewModel: EditReminderViewModel) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        val screenData = viewModel.editReminderState.screenData
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
                content = { Text(text = viewModel.cancelState.value.text.asCompose()) }
            )
            AppDialogActionButton(
                onClick = { viewModel.onEvent(EditReminderEvent.SaveReminder) },
                primaryButton = true,
                content = { Text(text = viewModel.saveState.value.text.asCompose()) }
            )
        }
    }
}

@Composable
private fun Content(viewModel: EditReminderViewModel) {
    val screenData = viewModel.editReminderState.screenData

    Row {
        OutlinedButton(
            modifier = Modifier.weight(0.5f),
            onClick = { viewModel.onEvent(EditReminderEvent.SelectReminderDate) },
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                content = { Text(text = screenData.dateText.asCompose()) }
            )
        }

        Spacer(modifier = Modifier.size(8.dp))

        OutlinedButton(
            modifier = Modifier.weight(0.5f),
            onClick = { viewModel.onEvent(EditReminderEvent.SelectReminderTime) },
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                content = { Text(text = screenData.timeText.asCompose()) }
            )
        }
    }

    DateDialog(viewModel)
    TimeDialog(viewModel)
}

@Composable
private fun DateDialog(viewModel: EditReminderViewModel) {
    val screenData = viewModel.editReminderState.screenData
    if (!screenData.showDateDialog) { return }

    val onDateSetListener = { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
        val event = EditReminderEvent.ReminderDateChanged(year, month, dayOfMonth)
        viewModel.onEvent(event)
    }

    val context = LocalContext.current

    DatePickerDialog(
        context,
        screenData.dateTimeDialogStyle(),
        onDateSetListener,
        screenData.dateYear,
        screenData.dateMonth,
        screenData.dateDayOfMonth
    ).apply {
        setOnCancelListener { viewModel.onEvent(EditReminderEvent.CancelSelectingReminderDate) }
        show()
    }
}

@Composable
private fun TimeDialog(viewModel: EditReminderViewModel) {
    val screenData = viewModel.editReminderState.screenData
    if (!screenData.showTimeDialog) { return }

    val context = LocalContext.current

    val onTimeSetListener = { _: TimePicker, hourOfDay: Int, minute: Int ->
        val event = EditReminderEvent.ReminderTimeChanged(hourOfDay, minute)
        viewModel.onEvent(event)
    }

    TimePickerDialog(
        context,
        screenData.dateTimeDialogStyle(),
        onTimeSetListener,
        screenData.timeHourOfDay,
        screenData.timeMinute,
        screenData.isTime24HourFormat()
    ).apply {
        setOnCancelListener { viewModel.onEvent(EditReminderEvent.CancelSelectingReminderTime) }
        show()
    }
}