package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import ru.sokolovromann.myshopping.R

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

@Composable
fun AppOpenNavigationButton(
    modifier: Modifier = Modifier,
    contentDescription: String? = stringResource(R.string.all_contentDescription_openNavigation),
    tint: Color = DefaultIconButtonTint,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

@Composable
fun AppCancelSearchButton(
    modifier: Modifier = Modifier,
    contentDescription: String? = stringResource(R.string.all_contentDescription_cancelSearch),
    tint: Color = DefaultIconButtonTint,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

private val DefaultIconButtonTint: Color
    @Composable
    get() = LocalContentColor.current.copy(
        alpha = LocalContentAlpha.current
    )