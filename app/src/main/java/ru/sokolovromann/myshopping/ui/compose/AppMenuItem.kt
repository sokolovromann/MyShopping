package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppMenuItem(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            AppMenuItemImpl(
                text = text,
                before = before,
                after = after
            )
        }
    }
}

@Composable
private fun AppMenuItemImpl(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
    before: @Composable (() -> Unit)? = null,
    after: @Composable (() -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        before?.let { it() }
        text()
        after?.let { it() }
    }
}