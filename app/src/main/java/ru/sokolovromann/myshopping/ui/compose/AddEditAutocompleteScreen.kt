package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.AddEditAutocompleteScreenEvent
import ru.sokolovromann.myshopping.ui.viewmodel.AddEditAutocompleteViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditAutocompleteEvent

@Composable
fun AddEditAutocompleteScreen(
    navController: NavController,
    viewModel: AddEditAutocompleteViewModel = hiltViewModel()
) {
    val state = viewModel.addEditAutocompleteState
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                AddEditAutocompleteScreenEvent.OnShowBackScreen -> {
                    focusManager.clearFocus(force = true)
                    navController.popBackStack()
                }
                AddEditAutocompleteScreenEvent.OnShowKeyboard -> {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    DefaultDialog(
        onDismissRequest = {
            val event = AddEditAutocompleteEvent.OnClickCancel
            viewModel.onEvent(event)
        },
        header = {
            Text(state.header.asCompose())
        },
        actionButtons = {
            TextButton(
                onClick = { viewModel.onEvent(AddEditAutocompleteEvent.OnClickCancel) },
                enabled = !state.waiting,
                content = { Text(stringResource(R.string.addEditAutocomplete_action_cancelSavingAutocomplete)) }
            )
            TextButton(
                onClick = { viewModel.onEvent(AddEditAutocompleteEvent.OnClickSave) },
                enabled = !state.waiting,
                content = { Text(stringResource(R.string.addEditAutocomplete_action_saveAutocomplete)) }
            )
        },
    ) {
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = state.nameValue,
            onValueChange = {
                val event = AddEditAutocompleteEvent.OnNameValueChanged(it)
                viewModel.onEvent(event)
            },
            label = {
                Text(text = stringResource(R.string.addEditAutocomplete_label_name))
            },
            error = { Text(text = stringResource(R.string.addEditAutocomplete_message_nameError)) },
            showError = state.nameError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.onEvent(AddEditAutocompleteEvent.OnClickSave) }
            )
        )
    }
}