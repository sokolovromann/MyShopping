package ru.sokolovromann.myshopping.ui.compose

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.annotation.StyleRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun AppDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    actionButtons: @Composable (RowScope.() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = MaterialTheme.colors.onSurface,
    content: @Composable ColumnScope.() -> Unit
) {
    AppDialogImpl(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        header = header,
        actionButtons = actionButtons,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(AppDialogContentPaddings),
            content = { content() }
        )
    }
}

@Composable
fun AppDatePickerDialog(
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
fun AppTimePickerDialog(
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
private fun AppDialogImpl(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    actionButtons: @Composable (RowScope.() -> Unit)? = null,
    backgroundColor: Color,
    contentColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(modifier = Modifier
            .padding(calculateDialogPaddings())
            .background(color = backgroundColor)
            .then(modifier)
        ) {
            header?.let {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDialogHeaderPaddings),
                    content = { ProvideAppDialogHeaderTextStyle(contentColor) { it() } }
                )
            }

            content()

            actionButtons?.let {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDialogActionButtonsPaddings),
                    content = { it() }
                )
            }
        }
    }
}

@Composable
private fun ProvideAppDialogHeaderTextStyle(contentColor: Color, content: @Composable () -> Unit) {
    ProvideTextStyle(
        value = MaterialTheme.typography.h6.copy(color = contentColor),
        content = content
    )
}

@Composable
private fun calculateDialogPaddings(): PaddingValues {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val horizontal: Dp = (screenWidth - screenWidth * 0.93f).dp

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val vertical: Dp = (screenHeight - screenHeight * 0.93f).dp

    return PaddingValues(
        horizontal = horizontal,
        vertical = vertical
    )
}

private val AppDialogHeaderPaddings = PaddingValues(
    start = 24.dp,
    top = 24.dp,
    end = 24.dp,
    bottom = 16.dp
)

private val AppDialogActionButtonsPaddings = PaddingValues(
    horizontal = 24.dp,
    vertical = 16.dp
)

private val AppDialogContentPaddings = PaddingValues(
    horizontal = 24.dp
)