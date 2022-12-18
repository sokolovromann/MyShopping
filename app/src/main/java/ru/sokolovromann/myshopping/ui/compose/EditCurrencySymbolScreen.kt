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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.ui.compose.event.EditCurrencySymbolScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.viewmodel.EditCurrencySymbolViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditCurrencySymbolEvent

@Composable
fun EditCurrencySymbolScreen(
    navController: NavController,
    viewModel: EditCurrencySymbolViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                EditCurrencySymbolScreenEvent.ShowBackScreen -> navController.popBackStack()
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
        onDismissRequest = { viewModel.onEvent(EditCurrencySymbolEvent.CancelSavingCurrencySymbol) },
        header = { Text(text = viewModel.headerState.value.text.asCompose()) },
        actionButtons = { ActionButtons(viewModel) },
        content = { Content(viewModel, focusRequester) }
    )
}

@Composable
private fun ActionButtons(viewModel: EditCurrencySymbolViewModel) {
    AppDialogActionButton(
        onClick = { viewModel.onEvent(EditCurrencySymbolEvent.CancelSavingCurrencySymbol) },
        content = { Text(text = viewModel.cancelState.value.text.asCompose()) }
    )
    AppDialogActionButton(
        onClick = { viewModel.onEvent(EditCurrencySymbolEvent.SaveCurrencySymbol) },
        primaryButton = true,
        content = { Text(text = viewModel.saveState.value.text.asCompose()) }
    )
}

@Composable
private fun Content(viewModel: EditCurrencySymbolViewModel, focusRequester: FocusRequester) {
    val symbolField = viewModel.symbolState.currentData
    OutlinedAppTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = symbolField.text,
        valueFontSize = symbolField.textFontSize,
        onValueChange = {
            val event = EditCurrencySymbolEvent.CurrencySymbolChanged(it)
            viewModel.onEvent(event)
        },
        label = { Text(text = symbolField.label.text.asCompose()) },
        error = { Text(text = (symbolField.error?.text ?: UiText.Nothing).asCompose()) },
        showError = symbolField.error?.text != null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { viewModel.onEvent(EditCurrencySymbolEvent.SaveCurrencySymbol) }
        )
    )
}