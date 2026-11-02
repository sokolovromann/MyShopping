package ru.sokolovromann.myshopping.feature.dictionary.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.component.SimpleScaffold
import ru.sokolovromann.myshopping.feature.dictionary.model.AddEditSuggestionState
import ru.sokolovromann.myshopping.feature.dictionary.viewmodel.AddEditSuggestionViewModel

@Composable
fun AddEditSuggestionScreen(addEditSuggestionViewModel: AddEditSuggestionViewModel = hiltViewModel()) {
    val addEditSuggestionState by addEditSuggestionViewModel
        .addEditSuggestionState.collectAsStateWithLifecycle()
    AddEditSuggestionScreenImpl(
        onBackClick = { addEditSuggestionViewModel.onBackClick() },
        onSaveSuggestionClick = { addEditSuggestionViewModel.onSaveSuggestionClick() },
        onDeleteFabricClick = { addEditSuggestionViewModel.onDeleteFabricClick(it) },
        onSuggestionNameChange = { addEditSuggestionViewModel.onSuggestionNameChange(it) },
        addEditSuggestionState = addEditSuggestionState
    )
}

@Composable
private fun AddEditSuggestionScreenImpl(
    onBackClick: () -> Unit,
    onSaveSuggestionClick: () -> Unit,
    onDeleteFabricClick: (UID) -> Unit,
    onSuggestionNameChange: (TextFieldValue) -> Unit,
    addEditSuggestionState: AddEditSuggestionState
) {
    SimpleScaffold(
        topBar = {
            AddEditSuggestionTopAppBar(
                onBackClick = onBackClick,
                onSaveSuggestionClick = onSaveSuggestionClick
            )
        },
        onBackClick = onBackClick,
        content = {
            AddEditSuggestionContent(
                onSuggestionNameChange = onSuggestionNameChange,
                onDoneClick = onSaveSuggestionClick,
                onDeleteFabricClick = onDeleteFabricClick,
                addEditSuggestionState = addEditSuggestionState
            )
        }
    )
}