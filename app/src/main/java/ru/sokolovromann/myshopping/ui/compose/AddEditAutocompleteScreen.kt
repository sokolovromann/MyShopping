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
import ru.sokolovromann.myshopping.ui.utils.toTextField
import ru.sokolovromann.myshopping.ui.viewmodel.AddEditAutocompleteViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditAutocompleteEvent

@Composable
fun AddEditAutocompleteScreen(
    navController: NavController,
    viewModel: AddEditAutocompleteViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                AddEditAutocompleteScreenEvent.ShowBackScreen -> {
                    navController.popBackStack()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.keyboardFlow.collect {
            if (it) {
                focusRequester.requestFocus()
            } else {
                focusManager.clearFocus(force = true)
            }
        }
    }

    AppDialog(
        onDismissRequest = { viewModel.onEvent(AddEditAutocompleteEvent.CancelSavingAutocomplete) },
        header = { Text(text = viewModel.addEditAutocompleteState.screenData.headerText.asCompose()) },
        actionButtons = { AddEditAutocompleteActionButtons(viewModel) },
        content = { Content(viewModel, focusRequester) }
    )
}

@Composable
private fun AddEditAutocompleteActionButtons(viewModel: AddEditAutocompleteViewModel) {
    AppDialogActionButton(
        onClick = { viewModel.onEvent(AddEditAutocompleteEvent.CancelSavingAutocomplete) },
        content = { Text(text = stringResource(R.string.addEditAutocomplete_action_cancelSavingAutocomplete)) }
    )
    AppDialogActionButton(
        onClick = { viewModel.onEvent(AddEditAutocompleteEvent.SaveAutocomplete) },
        primaryButton = true,
        content = { Text(text = stringResource(R.string.addEditAutocomplete_action_saveAutocomplete)) }
    )
}

@Composable
private fun Content(
    viewModel: AddEditAutocompleteViewModel,
    focusRequester: FocusRequester
) {
    val data = viewModel.addEditAutocompleteState.screenData

    OutlinedAppTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = data.nameValue,
        valueFontSize = data.fontSize.toTextField().sp,
        onValueChange = {
            val event = AddEditAutocompleteEvent.NameChanged(it)
            viewModel.onEvent(event)
        },
        label = { Text(text = stringResource(R.string.addEditAutocomplete_label_name)) },
        error = { Text(text = data.nameError.asCompose()) },
        showError = data.showNameError,
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