package ru.sokolovromann.myshopping.core.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.sokolovromann.myshopping.core.ui.R

@Composable
fun SelectedItemRightIcon() {
    Icon(
        imageVector = Icons.Filled.Check,
        contentDescription = stringResource(R.string.text_selected),
        tint = MaterialTheme.colorScheme.primary
    )
}