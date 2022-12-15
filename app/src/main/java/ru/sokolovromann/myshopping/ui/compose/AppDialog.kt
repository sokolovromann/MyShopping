package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AppDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    title: @Composable (ColumnScope.() -> Unit)? = null,
    actionButtons: @Composable (RowScope.() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = MaterialTheme.colors.onSurface,
    content: @Composable ColumnScope.() -> Unit
) {
    AppDialogImpl(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = title,
        actionButtons = actionButtons,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppDialogContentPaddings),
            content = { content() }
        )
    }
}

@Composable
private fun AppDialogImpl(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    title: @Composable (ColumnScope.() -> Unit)? = null,
    actionButtons: @Composable (RowScope.() -> Unit)? = null,
    backgroundColor: Color,
    contentColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(modifier = Modifier
            .background(color = backgroundColor)
            .then(modifier)
        ) {
            title?.let {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppDialogTitlePaddings),
                    content = { ProvideAppDialogTitleTextStyle(contentColor) { it() } }
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
private fun ProvideAppDialogTitleTextStyle(contentColor: Color, content: @Composable () -> Unit) {
    ProvideTextStyle(
        value = MaterialTheme.typography.h5.copy(color = contentColor),
        content = content
    )
}

private val AppDialogTitlePaddings = PaddingValues(
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