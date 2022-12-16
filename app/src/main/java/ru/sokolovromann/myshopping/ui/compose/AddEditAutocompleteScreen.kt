package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.ui.compose.event.AddEditAutocompleteScreenEvent
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
        header = { Text(text = viewModel.headerState.value.text.asCompose()) },
        actionButtons = { ActionButtons(viewModel) },
        content = { Content(viewModel, focusRequester) }
    )
}

@Composable
private fun ActionButtons(viewModel: AddEditAutocompleteViewModel) {
    TextButton(
        onClick = { viewModel.onEvent(AddEditAutocompleteEvent.CancelSavingAutocomplete) },
        content = { AppText(data = viewModel.cancelState.value) }
    )
    Spacer(modifier = Modifier.size(4.dp))
    OutlinedButton(
        onClick = { viewModel.onEvent(AddEditAutocompleteEvent.SaveAutocomplete) },
        content = { AppText(data = viewModel.saveState.value) }
    )
}

@Composable
private fun Content(
    viewModel: AddEditAutocompleteViewModel,
    focusRequester: FocusRequester
) {
    OutlinedAppTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        state = viewModel.nameState,
        onValueChange = {
            val event = AddEditAutocompleteEvent.NameChanged(it)
            viewModel.onEvent(event)
        }
    )
}