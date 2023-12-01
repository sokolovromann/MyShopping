package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.sp
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
                AddEditAutocompleteScreenEvent.ShowBackScreen -> {
                    focusManager.clearFocus(force = true)
                    navController.popBackStack()
                }
                AddEditAutocompleteScreenEvent.ShowKeyboard -> {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    AppDialog(
        onDismissRequest = { viewModel.onEvent(AddEditAutocompleteEvent.CancelSavingAutocomplete) },
        header = { Text(text = state.header.asCompose()) },
        actionButtons = {
            AppDialogActionButton(
                onClick = { viewModel.onEvent(AddEditAutocompleteEvent.CancelSavingAutocomplete) },
                enabled = !state.waiting,
                content = {
                    Text(text = stringResource(R.string.addEditAutocomplete_action_cancelSavingAutocomplete))
                }
            )
            AppDialogActionButton(
                onClick = { viewModel.onEvent(AddEditAutocompleteEvent.SaveAutocomplete) },
                primaryButton = true,
                enabled = !state.waiting,
                content = {
                    Text(text = stringResource(R.string.addEditAutocomplete_action_saveAutocomplete))
                }
            )
        }
    ) {
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = state.nameValue,
            valueFontSize = state.fontSize.textField.sp,
            onValueChange = {
                val event = AddEditAutocompleteEvent.NameChanged(it)
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
                onDone = { viewModel.onEvent(AddEditAutocompleteEvent.SaveAutocomplete) }
            )
        )
    }
}