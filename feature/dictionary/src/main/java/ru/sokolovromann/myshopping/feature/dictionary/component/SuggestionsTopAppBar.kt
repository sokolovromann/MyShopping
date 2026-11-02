package ru.sokolovromann.myshopping.feature.dictionary.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.component.NavigationTopAppBar
import ru.sokolovromann.myshopping.core.ui.component.SelectedTopAppBar
import ru.sokolovromann.myshopping.feature.dictionary.R

@Composable
fun SuggestionsTopAppBar(
    onNavigationIconClick: () -> Unit,
    onEditSuggestionClick: () -> Unit,
    onDeleteSuggestionsClick: () -> Unit,
    onSelectAllSuggestionsClick: () -> Unit,
    onDeselectAllSuggestionsClick: () -> Unit,
    itemsSelected: Set<UID>
) {
    if (itemsSelected.isEmpty()) {
        SuggestionsNavigationTopAppBar(onNavigationIconClick)
    } else {
        SuggestionsSelectModeTopAppBar(
            onEditSuggestionClick = onEditSuggestionClick,
            onDeleteSuggestionsClick = onDeleteSuggestionsClick,
            onSelectAllSuggestionsClick = onSelectAllSuggestionsClick,
            onDeselectAllSuggestionsClick = onDeselectAllSuggestionsClick,
            count = itemsSelected.count()
        )
    }
}

@Composable
private fun SuggestionsNavigationTopAppBar(
    onNavigationIconClick: () -> Unit
) {
    NavigationTopAppBar(
        header = stringResource(R.string.dictionary_header),
        onNavigationIconClick = onNavigationIconClick
    )
}

@Composable
private fun SuggestionsSelectModeTopAppBar(
    onEditSuggestionClick: () -> Unit,
    onDeleteSuggestionsClick: () -> Unit,
    onSelectAllSuggestionsClick: () -> Unit,
    onDeselectAllSuggestionsClick: () -> Unit,
    count: Int,
) {
    SelectedTopAppBar(
        count = count,
        onCancelClick = onDeselectAllSuggestionsClick,
        actions = {
            if (count == 1) {
                IconButton(onClick = onEditSuggestionClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.dictionary_icon_edit_suggestion)
                    )
                }
            }
            IconButton(onClick = onDeleteSuggestionsClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.dictionary_icon_delete_suggestions)
                )
            }
            IconButton(onClick = onSelectAllSuggestionsClick) {
                Icon(
                    imageVector = Icons.Default.SelectAll,
                    contentDescription = stringResource(R.string.dictionary_icon_select_all_suggestions)
                )
            }
        }
    )
}