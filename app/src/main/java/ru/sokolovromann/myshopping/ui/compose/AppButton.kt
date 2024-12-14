package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.sokolovromann.myshopping.ui.model.UiIcon
import ru.sokolovromann.myshopping.ui.model.UiString

@Deprecated("Use IconButton with DefaultIcon")
@Composable
fun DefaultIconButton(
    icon: UiIcon,
    modifier: Modifier = Modifier,
    contentDescription: UiString? = null,
    enabled: Boolean = true,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    dropDownMenu: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Icon(
            painter = icon.asPainter(),
            contentDescription = contentDescription?.asCompose(),
            tint = tint
        )
        dropDownMenu?.let { it() }
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