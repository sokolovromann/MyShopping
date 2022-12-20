package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AppBottomAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    actionButtons: @Composable (RowScope.() -> Unit)? = null,
    contentColor: Color = contentColorFor(backgroundColor),
    content: @Composable (RowScope.() -> Unit)? = null
) {
    BottomAppBar(
        modifier = modifier,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    ) {
        content?.let { it() }
        actionButtons?.let {
            Spacer(modifier = Modifier.weight(1f))
            it()
        }
    }
}