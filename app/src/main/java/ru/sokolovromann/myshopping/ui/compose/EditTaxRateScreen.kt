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
import ru.sokolovromann.myshopping.ui.compose.event.EditTaxRateScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.viewmodel.EditTaxRateViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditTaxRateEvent

@Composable
fun EditTaxRateScreen(
    navController: NavController,
    viewModel: EditTaxRateViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                EditTaxRateScreenEvent.ShowBackScreen -> navController.popBackStack()
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
        onDismissRequest = { viewModel.onEvent(EditTaxRateEvent.CancelSavingTaxRate) },
        header = { Text(text = viewModel.headerState.value.text.asCompose()) },
        actionButtons = { ActionButtons(viewModel) },
        content = { Content(viewModel, focusRequester) }
    )
}

@Composable
private fun ActionButtons(viewModel: EditTaxRateViewModel) {
    AppDialogActionButton(
        onClick = { viewModel.onEvent(EditTaxRateEvent.CancelSavingTaxRate) },
        content = { Text(text = viewModel.cancelState.value.text.asCompose()) }
    )
    AppDialogActionButton(
        onClick = { viewModel.onEvent(EditTaxRateEvent.SaveTaxRate) },
        primaryButton = true,
        content = { Text(text = viewModel.saveState.value.text.asCompose()) }
    )
}

@Composable
private fun Content(viewModel: EditTaxRateViewModel, focusRequester: FocusRequester) {
    val taxRateField = viewModel.taxRateState.currentData
    OutlinedAppTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = taxRateField.text,
        valueFontSize = taxRateField.textFontSize,
        onValueChange = {
            val event = EditTaxRateEvent.TaxRateChanged(it)
            viewModel.onEvent(event)
        },
        label = { Text(text = taxRateField.label.text.asCompose()) },
        error = { Text(text = (taxRateField.error?.text ?: UiText.Nothing).asCompose()) },
        showError = taxRateField.error?.text != null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { viewModel.onEvent(EditTaxRateEvent.SaveTaxRate) }
        )
    )
}