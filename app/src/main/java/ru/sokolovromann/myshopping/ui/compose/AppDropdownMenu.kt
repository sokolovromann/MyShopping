package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
fun AppDropdownMenu(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    properties: PopupProperties = PopupProperties(focusable = true),
    header: @Composable (() -> Unit)? = null,
    items: @Composable ColumnScope.() -> Unit
) {
    DropdownMenu(
        modifier = Modifier
            .background(color = backgroundColor)
            .then(modifier),
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        header?.let {
            Column(
                modifier = Modifier.padding(AppDropdownMenuHeaderPaddings),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                ProvideAppDropdownMenuHeaderTextStyle(
                    contentColor = contentColor,
                    content = it
                )
            }
        }

        items()
    }
}

@Composable
private fun ProvideAppDropdownMenuHeaderTextStyle(
    contentColor: Color,
    content: @Composable () -> Unit
) {
    ProvideTextStyle(
        value = MaterialTheme.typography.subtitle1.copy(
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        ),
        content = content
    )
}

private val AppDropdownMenuHeaderPaddings = PaddingValues(
    horizontal = 16.dp,
    vertical = 8.dp
)