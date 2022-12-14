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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.EditCurrencySymbolScreenEvent
import ru.sokolovromann.myshopping.ui.utils.toTextField
import ru.sokolovromann.myshopping.ui.viewmodel.EditCurrencySymbolViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditCurrencySymbolEvent

@Composable
fun EditCurrencySymbolScreen(
    navController: NavController,
    viewModel: EditCurrencySymbolViewModel = hiltViewModel()
) {
    val screenData = viewModel.editCurrencySymbolState.screenData
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                EditCurrencySymbolScreenEvent.ShowBackScreen -> {
                    focusManager.clearFocus(force = true)
                    navController.popBackStack()
                }

                EditCurrencySymbolScreenEvent.ShowKeyboard -> {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    AppDialog(
        onDismissRequest = { viewModel.onEvent(EditCurrencySymbolEvent.CancelSavingCurrencySymbol) },
        header = { Text(text = stringResource(R.string.editCurrencySymbol_header)) },
        actionButtons = {
            AppDialogActionButton(
                onClick = { viewModel.onEvent(EditCurrencySymbolEvent.CancelSavingCurrencySymbol) },
                content = {
                    Text(text = stringResource(R.string.editCurrencySymbol_action_cancelSavingCurrencySymbol))
                }
            )
            AppDialogActionButton(
                onClick = { viewModel.onEvent(EditCurrencySymbolEvent.SaveCurrencySymbol) },
                primaryButton = true,
                content = {
                    Text(text = stringResource(R.string.editCurrencySymbol_action_saveCurrencySymbol))
                }
            )
        }
    ) {
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = screenData.symbolValue,
            valueFontSize = screenData.fontSize.toTextField().sp,
            onValueChange = {
                val event = EditCurrencySymbolEvent.CurrencySymbolChanged(it)
                viewModel.onEvent(event)
            },
            label = { Text(text = stringResource(R.string.editCurrencySymbol_label_symbol)) },
            error = { Text(text = stringResource(R.string.editCurrencySymbol_message_symbolError)) },
            showError = screenData.showSymbolError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.onEvent(EditCurrencySymbolEvent.SaveCurrencySymbol) }
            )
        )
    }
}