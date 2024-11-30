package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.sokolovromann.myshopping.ui.model.UiIcon
import ru.sokolovromann.myshopping.ui.model.UiString

@Composable
fun DefaultIconButton(
    icon: UiIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: UiString? = null,
    tint: Color = LocalContentColor.current
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = true
    ) {
        Icon(
            painter = icon.asPainter(),
            contentDescription = contentDescription?.asCompose(),
            tint = tint
        )
    }
}

@Composable
fun AppDialogActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    primaryButton: Boolean = false,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    if (primaryButton) {
        Button(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled,
            content = content
        )
    } else {
        TextButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            content = content
        )
    }
}