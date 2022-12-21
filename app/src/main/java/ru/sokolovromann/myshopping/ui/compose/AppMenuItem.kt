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
fun AppMenuItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = MaterialTheme.colors.onSurface,
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
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
                .defaultMinSize(minHeight = AppMenuItemMinHeight)
                .background(color = backgroundColor),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            AppMenuItemImpl(
                contentColor = contentColor,
                before = before,
                after = after,
                text = text,
            )
        }
    }
}

@Composable
private fun AppMenuItemImpl(
    contentColor: Color,
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
    text: @Composable () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        before?.let {
            it()
            Spacer(modifier = Modifier.padding(AppMenuItemBeforePaddings))
        }

        ProvideAppMenuItemTextStyle(
            contentColor = contentColor,
            content = text
        )

        after?.let {
            Spacer(modifier = Modifier
                .weight(1f)
                .padding(AppMenuItemAfterPaddings)
            )
            it()
        }
    }
}

@Composable
private fun ProvideAppMenuItemTextStyle(
    contentColor: Color,
    content: @Composable () -> Unit
) {
    ProvideTextStyle(
        value = MaterialTheme.typography.body1.copy(color = contentColor),
        content = content
    )
}

private val AppMenuItemMinHeight: Dp = 48.dp
private val AppMenuItemBeforePaddings = PaddingValues(
    horizontal = 8.dp
)
private val AppMenuItemAfterPaddings = PaddingValues(
    horizontal = 8.dp
)