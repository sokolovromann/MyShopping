package ru.sokolovromann.myshopping.ui.compose

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.StyleRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun DefaultDialog(
    onDismissRequest: () -> Unit,
    header: @Composable () -> Unit,
    actionButtons: @Composable RowScope.() -> Unit,
    backgroundColor: Color = MaterialTheme.colors.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    DefaultDialogSurface(onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .background(backgroundColor)
                .padding(all = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            ProvideTextStyle(
                value = MaterialTheme.typography.h6.copy(
                    color = contentColorFor(backgroundColor)
                ),
                content = header
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 16.dp),
                content = content
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                content = actionButtons
            )
        }
    }
}

@Composable
fun DefaultDatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateChanged: (Int, Int, Int) -> Unit,
    @StyleRes dialogStyle: Int,
    year: Int,
    monthOfYear: Int,
    dayOfMonth: Int
) {
    val context = LocalContext.current
    val onDateSetListener = { _: DatePicker, y: Int, m: Int, d: Int -> onDateChanged(y, m, d) }

    DatePickerDialog(context, dialogStyle, onDateSetListener, year, monthOfYear, dayOfMonth).apply {
        setOnCancelListener { onDismissRequest() }
        show()
    }
}

@Composable
fun DefaultTimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeChanged: (Int, Int) -> Unit,
    @StyleRes dialogStyle: Int,
    hourOfDay: Int,
    minute: Int,
    is24HourFormat: Boolean
) {
    val context = LocalContext.current
    val onTimeSetListener = { _: TimePicker, h: Int, m: Int -> onTimeChanged(h, m) }

    TimePickerDialog(context, dialogStyle, onTimeSetListener, hourOfDay, minute, is24HourFormat).apply {
        setOnCancelListener { onDismissRequest() }
        show()
    }
}

@Composable
private fun DefaultDialogSurface(
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier.padding(calculateDialogPadding()),
            content = content
        )
    }
}

@Composable
private fun calculateDialogPadding(): PaddingValues {
    val maxScreenOccupiedPercent = 0.93f
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    return PaddingValues(
        horizontal = (screenWidth - screenWidth * maxScreenOccupiedPercent).dp,
        vertical = (screenHeight - screenHeight * maxScreenOccupiedPercent).dp
    )
}