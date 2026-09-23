package ru.sokolovromann.myshopping.feature.dictionary

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import ru.sokolovromann.myshopping.core.ui.model.NavigationItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.component.LoadingContent
import ru.sokolovromann.myshopping.core.ui.component.NavigationDrawer
import ru.sokolovromann.myshopping.core.ui.component.NavigationTopAppBar
import ru.sokolovromann.myshopping.core.ui.component.SelectedTopAppBar
import ru.sokolovromann.myshopping.core.ui.component.SimpleScaffold
import ru.sokolovromann.myshopping.core.ui.component.isCloseableNavigationDrawer
import ru.sokolovromann.myshopping.core.ui.component.rememberDefaultDrawerState

@Composable
fun DictionaryScreen(viewModel: DictionaryViewModel = hiltViewModel()) {
    val dictionaryState by viewModel.dictionaryState.collectAsStateWithLifecycle()
    DictionaryScreenImpl(
        onNavigationItemSelected = { viewModel.onNavigationItemSelected(it) },
        onBackClick = { viewModel.onBackClick() },
        onAddSuggestionClick = { viewModel.onAddSuggestionClick() },
        onEditSuggestionClick = { viewModel.onEditSuggestionClick() },
        onDeleteSuggestionsClick = { viewModel.onDeleteSuggestionsClick() },
        onSelectAllSuggestionsClick = { viewModel.onSelectAllSuggestionsClick() },
        onSuggestionItemClick = { viewModel.onSuggestionItemClick(it) },
        onSuggestionItemLongClick = { viewModel.onSuggestionItemLongClick(it) },
        state = dictionaryState
    )
}

@Composable
private fun DictionaryScreenImpl(
    onNavigationItemSelected: (NavigationItem) -> Unit,
    onBackClick: () -> Unit,
    onAddSuggestionClick: () -> Unit,
    onEditSuggestionClick: () -> Unit,
    onDeleteSuggestionsClick: () -> Unit,
    onSelectAllSuggestionsClick: () -> Unit,
    onSuggestionItemClick: (UID) -> Unit,
    onSuggestionItemLongClick: (UID) -> Unit,
    state: DictionaryState
) {
    val drawerState = rememberDefaultDrawerState()
    val isCloseableNavigationDrawer = isCloseableNavigationDrawer()
    val scope = rememberCoroutineScope()
    fun openDrawer() = scope.launch { drawerState.open() }
    fun closeDrawer() = scope.launch { drawerState.close() }

    NavigationDrawer(
        drawerState = drawerState,
        initialItem = NavigationItem.Dictionary,
        onItemClick = {
            if (isCloseableNavigationDrawer) closeDrawer()
            onNavigationItemSelected(it)
        }
    ) {
        SimpleScaffold(
            topBar = {
                if (state.suggestionsState.isSelectMode) {
                    SelectedTopAppBar(
                        count = state.suggestionsState.selectedCount,
                        onCancelClick = { onBackClick() },
                        actions = {
                            if (state.suggestionsState.isSingleSelectMode) {
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
                } else {
                    NavigationTopAppBar(
                        header = stringResource(R.string.dictionary_header),
                        onNavigationIconClick = { openDrawer() }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAddSuggestionClick) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = stringResource(R.string.dictionary_icon_add_suggestion)
                    )
                }
            },
            onBackClick = {
                if (isCloseableNavigationDrawer && drawerState.isOpen) {
                    closeDrawer()
                } else {
                    onBackClick()
                }
            },
            content = {
                if (state.isLoading) {
                    LoadingContent()
                } else {
                    SuggestionsContent(
                        onItemClick = onSuggestionItemClick,
                        onItemLongClick = onSuggestionItemLongClick,
                        state = state.suggestionsState
                    )
                }
            }
        )
    }
}