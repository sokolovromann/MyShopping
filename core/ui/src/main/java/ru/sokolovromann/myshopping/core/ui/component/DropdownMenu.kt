package ru.sokolovromann.myshopping.core.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DropdownMenuTitle(text: String) {
    Text(
        modifier = Modifier.padding(all = 16.dp),
        text = text,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun DropdownMenuSelectableItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val trailingIcon  = if (selected) {
        @Composable { SelectedItemRightIcon() }
    } else null

    DropdownMenuItem(
        text = { Text(text) },
        onClick = onClick,
        trailingIcon = trailingIcon
    )
}