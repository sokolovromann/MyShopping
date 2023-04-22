package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppDropdownMenuItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colors.onSurface,
    left: @Composable (() -> Unit)? = null,
    right: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit,
) {
    DropdownMenuItem(
        modifier = modifier,
        contentPadding = contentPadding,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = AppDropdownMenuItemMinHeight)
                .background(color = backgroundColor),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            AppDropdownMenuItemImpl(
                contentColor = contentColor,
                left = left,
                right = right,
                text = text,
            )
        }
    }
}

@Composable
private fun AppDropdownMenuItemImpl(
    contentColor: Color,
    left: @Composable (() -> Unit)? = null,
    right: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        left?.let {
            it()
            Spacer(modifier = Modifier.padding(AppDropdownMenuItemBeforePaddings))
        }

        ProvideAppDropdownMenuItemTextStyle(
            contentColor = contentColor,
            content = text
        )

        right?.let {
            Spacer(modifier = Modifier
                .weight(1f)
                .padding(AppDropdownMenuItemAfterPaddings)
            )
            it()
        }
    }
}

@Composable
private fun ProvideAppDropdownMenuItemTextStyle(
    contentColor: Color,
    content: @Composable () -> Unit
) {
    ProvideTextStyle(
        value = MaterialTheme.typography.body1.copy(color = contentColor),
        content = content
    )
}

private val AppDropdownMenuItemMinHeight: Dp = 48.dp
private val AppDropdownMenuItemBeforePaddings = PaddingValues(
    horizontal = 8.dp
)
private val AppDropdownMenuItemAfterPaddings = PaddingValues(
    horizontal = 8.dp
)