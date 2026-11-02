package ru.sokolovromann.myshopping.feature.dictionary.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.sokolovromann.myshopping.feature.dictionary.R

@Composable
fun SuggestionsFab(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.AddCircleOutline,
            contentDescription = stringResource(R.string.dictionary_icon_add_suggestion)
        )
    }
}