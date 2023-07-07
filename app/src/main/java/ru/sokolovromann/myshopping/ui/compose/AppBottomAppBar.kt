package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AppBottomAppBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    actionButtons: @Composable (RowScope.() -> Unit)? = null,
    contentColor: Color = contentColorFor(backgroundColor),
    content: @Composable (RowScope.() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AppBottomAppBarMinHeight),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Column {
            Divider()
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                content?.let {
                    val contentModifier = if (actionButtons == null) {
                        Modifier
                    } else {
                        Modifier.weight(0.99f)
                    }
                    Row(
                        modifier = contentModifier,
                        content = it
                    )
                }
                actionButtons?.let {
                    val actionModifier = if (content == null) {
                        Modifier
                    } else {
                        Modifier.weight(0.01f)
                    }
                    Spacer(modifier = actionModifier)
                    it()
                }
            }
        }
    }
}

private val AppBottomAppBarMinHeight = 56.dp