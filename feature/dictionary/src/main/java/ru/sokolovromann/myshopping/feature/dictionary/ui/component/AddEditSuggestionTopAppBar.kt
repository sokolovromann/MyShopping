package ru.sokolovromann.myshopping.feature.dictionary.ui.component

import androidx.compose.runtime.Composable
import ru.sokolovromann.myshopping.core.ui.component.AddEditTopAppBar

@Composable
fun AddEditSuggestionTopAppBar(
    onBackClick: () -> Unit,
    onSaveSuggestionClick: () -> Unit,
) {
    AddEditTopAppBar(
        onCancelClick = onBackClick,
        onSaveClick = onSaveSuggestionClick
    )
}