package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppDialogActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    primaryButton: Boolean = false,
    content: @Composable RowScope.() -> Unit
) {
    if (primaryButton) {
        Button(
            modifier = modifier,
            onClick = onClick,
            content = content
        )
    } else {
        TextButton(
            onClick = onClick,
            modifier = modifier,
            content = content
        )
    }
}