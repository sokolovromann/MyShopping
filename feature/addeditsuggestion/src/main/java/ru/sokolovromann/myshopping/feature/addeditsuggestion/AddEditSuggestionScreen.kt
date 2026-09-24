package ru.sokolovromann.myshopping.feature.addeditsuggestion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.sokolovromann.myshopping.core.ui.component.AddEditTopAppBar
import ru.sokolovromann.myshopping.core.ui.component.ErrorSupportingText
import ru.sokolovromann.myshopping.core.ui.component.SimpleScaffold

@Composable
fun AddEditSuggestionScreen(viewModel: AddEditSuggestionViewModel = hiltViewModel()) {
    val addEditSuggestionState by viewModel.addEditSuggestionState.collectAsStateWithLifecycle()
    AddEditSuggestionScreenImpl(
        onBackClick = { viewModel.onBackClick() },
        onSaveSuggestionClick = { viewModel.onSaveSuggestionClick() },
        onSuggestionNameChange = { viewModel.onSuggestionNameChange(it) },
        state = addEditSuggestionState
    )
}

@Composable
private fun AddEditSuggestionScreenImpl(
    onBackClick: () -> Unit,
    onSaveSuggestionClick: () -> Unit,
    onSuggestionNameChange: (TextFieldValue) -> Unit,
    state: AddEditSuggestionState
) {
    SimpleScaffold(
        topBar = {
            AddEditTopAppBar(
                onCancelClick = onBackClick,
                onSaveClick = onSaveSuggestionClick
            )
        },
        onBackClick = onBackClick
    ) {
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) {
            if (state.isNewSuggestion) {
                focusRequester.requestFocus()
            }
        }

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(all = 8.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = state.name,
                onValueChange = onSuggestionNameChange,
                label = {
                    Text(stringResource(R.string.add_edit_suggestion_label_name))
                },
                isError = state.isNameError,
                supportingText = {
                    if (state.isNameError) {
                        ErrorSupportingText(stringResource(R.string.add_edit_suggestion_supporting_text_name_error))
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onSaveSuggestionClick() }
                ),
                singleLine = true
            )
        }
    }
}