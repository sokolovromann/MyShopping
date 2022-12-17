package ru.sokolovromann.myshopping.ui.compose

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.text.format.DateFormat
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.ui.compose.event.EditReminderScreenEvent
import ru.sokolovromann.myshopping.ui.viewmodel.EditReminderViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditReminderEvent
import java.util.*

@ExperimentalFoundationApi
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
        header = { Text(text = viewModel.headerState.value.text.asCompose()) },
        actionButtons = { ActionButtons(viewModel) },
        content = { Content(viewModel) }
    )
}

@ExperimentalFoundationApi
@Composable
private fun ActionButtons(viewModel: EditReminderViewModel) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Center
    ) {
        viewModel.deleteState.value?.let {
            AppDialogActionButton(
                onClick = { viewModel.onEvent(EditReminderEvent.DeleteReminder) },
                content = { Text(text = it.text.asCompose()) }
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

@ExperimentalFoundationApi
@Composable
private fun Content(viewModel: EditReminderViewModel) {
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        onClick = { viewModel.onEvent(EditReminderEvent.SelectReminderDate) },
        content = { AppText(data = viewModel.dateState.value) }
    )
    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { viewModel.onEvent(EditReminderEvent.SelectReminderTime) },
        content = { AppText(data = viewModel.timeState.value) }
    )

    DateDialog(viewModel)
    TimeDialog(viewModel)
}

@ExperimentalFoundationApi
@Composable
private fun DateDialog(viewModel: EditReminderViewModel) {
    if (!viewModel.dateDialogState.value) {
        return
    }

    val reminderState = viewModel.reminderState.value
    val year = reminderState.get(Calendar.YEAR)
    val month = reminderState.get(Calendar.MONTH)
    val dayOfMonth = reminderState.get(Calendar.DAY_OF_MONTH)

    val onDateSetListener = { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
        val event = EditReminderEvent.ReminderDateChanged(year, month, dayOfMonth)
        viewModel.onEvent(event)
    }

    val context = LocalContext.current

    val style: Int = if (isSystemInDarkTheme()) {
        ru.sokolovromann.myshopping.R.style.Theme_MyShopping_DateTimePicket_Dark
    } else {
        ru.sokolovromann.myshopping.R.style.Theme_MyShopping_DateTimePicket_Light
    }

    DatePickerDialog(context, style, onDateSetListener, year, month, dayOfMonth).apply {
        setOnCancelListener {
            viewModel.onEvent(EditReminderEvent.CancelSelectingReminderDate)
        }
        show()
    }
}

@ExperimentalFoundationApi
@Composable
private fun TimeDialog(viewModel: EditReminderViewModel) {
    if (!viewModel.timeDialogState.value) {
        return
    }

    val reminderState = viewModel.reminderState.value

    val context = LocalContext.current
    val hourOfDay = reminderState.get(Calendar.HOUR_OF_DAY)
    val minute = reminderState.get(Calendar.MINUTE)
    val is24HourView = DateFormat.is24HourFormat(context)

    val onTimeSetListener = { _: TimePicker, hourOfDay: Int, minute: Int ->
        val event = EditReminderEvent.ReminderTimeChanged(hourOfDay, minute)
        viewModel.onEvent(event)
    }

    val style: Int = if (isSystemInDarkTheme()) {
        ru.sokolovromann.myshopping.R.style.Theme_MyShopping_DateTimePicket_Dark
    } else {
        ru.sokolovromann.myshopping.R.style.Theme_MyShopping_DateTimePicket_Light
    }

    TimePickerDialog(context, style, onTimeSetListener, hourOfDay, minute, is24HourView)
        .apply {
            setOnCancelListener {
                viewModel.onEvent(EditReminderEvent.CancelSelectingReminderTime)
            }
            show()
        }
}