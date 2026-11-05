package ru.sokolovromann.myshopping.feature.dictionary.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.component.NavigationDrawer
import ru.sokolovromann.myshopping.core.ui.component.SimpleScaffold
import ru.sokolovromann.myshopping.core.ui.component.isCloseableNavigationDrawer
import ru.sokolovromann.myshopping.core.ui.component.rememberDefaultDrawerState
import ru.sokolovromann.myshopping.core.ui.model.NavigationItem
import ru.sokolovromann.myshopping.feature.dictionary.model.SuggestionsState
import ru.sokolovromann.myshopping.feature.dictionary.viewmodel.SuggestionsViewModel

@Composable
fun SuggestionsScreen(suggestionsViewModel: SuggestionsViewModel = hiltViewModel()) {
    val suggestionsState by suggestionsViewModel.suggestionsState.collectAsStateWithLifecycle()
    SuggestionsScreenImpl(
        onNavigationItemSelected = { suggestionsViewModel.onNavigationItemSelected(it) },
        onBackClick = { suggestionsViewModel.onBackClick() },
        onAddSuggestionClick = { suggestionsViewModel.onAddSuggestionClick() },
        onEditSuggestionClick = { suggestionsViewModel.onEditSuggestionClick() },
        onDeleteSuggestionsClick = { suggestionsViewModel.onDeleteSuggestionsClick() },
        onSelectAllSuggestionsClick = { suggestionsViewModel.onSelectAllSuggestionsClick() },
        onSuggestionItemClick = { suggestionsViewModel.onSuggestionItemClick(it) },
        onSuggestionItemLongClick = { suggestionsViewModel.onSuggestionItemLongClick(it) },
        suggestionsState = suggestionsState
    )
}

@Composable
private fun SuggestionsScreenImpl(
    onNavigationItemSelected: (NavigationItem) -> Unit,
    onBackClick: () -> Unit,
    onAddSuggestionClick: () -> Unit,
    onEditSuggestionClick: () -> Unit,
    onDeleteSuggestionsClick: () -> Unit,
    onSelectAllSuggestionsClick: () -> Unit,
    onSuggestionItemClick: (UID) -> Unit,
    onSuggestionItemLongClick: (UID) -> Unit,
    suggestionsState: SuggestionsState
) {
    val drawerState = rememberDefaultDrawerState()
    val isCloseableNavigationDrawer = isCloseableNavigationDrawer()
    val scope = rememberCoroutineScope()
    fun openDrawer() = scope.launch { drawerState.open() }
    fun closeDrawer() = scope.launch { drawerState.close() }

    NavigationDrawer(
        drawerState = drawerState,
        initialItem = NavigationItem.Suggestions,
        onItemClick = {
            if (isCloseableNavigationDrawer) closeDrawer()
            onNavigationItemSelected(it)
        }
    ) {
        SimpleScaffold(
            topBar = {
                SuggestionsTopAppBar(
                    onNavigationIconClick = { openDrawer() },
                    onEditSuggestionClick = onEditSuggestionClick,
                    onDeleteSuggestionsClick = onDeleteSuggestionsClick,
                    onSelectAllSuggestionsClick = onSelectAllSuggestionsClick,
                    onDeselectAllSuggestionsClick = onBackClick,
                    itemsSelected = suggestionsState.itemsSelected
                )
            },
            onBackClick = {
                if (isCloseableNavigationDrawer && drawerState.isOpen) {
                    closeDrawer()
                } else {
                    onBackClick()
                }
            },
            floatingActionButton = {
                SuggestionsFab { onAddSuggestionClick() }
            },
            content = {
                SuggestionsContent(
                    onItemClick = onSuggestionItemClick,
                    onItemLongClick = onSuggestionItemLongClick,
                    suggestionsState = suggestionsState
                )
            }
        )
    }
}