package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.EditCurrencySymbolScreenEvent
import ru.sokolovromann.myshopping.ui.utils.updateProductsWidgets
import ru.sokolovromann.myshopping.ui.viewmodel.EditCurrencySymbolViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditCurrencySymbolEvent

@Composable
fun EditCurrencySymbolScreen(
    navController: NavController,
    viewModel: EditCurrencySymbolViewModel = hiltViewModel()
) {
    val state = viewModel.editCurrencySymbolState
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                EditCurrencySymbolScreenEvent.OnShowBackScreen -> {
                    updateProductsWidgets(context)
                    focusManager.clearFocus(force = true)
                    navController.popBackStack()
                }

                EditCurrencySymbolScreenEvent.OnShowKeyboard -> {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    AppDialog(
        onDismissRequest = { viewModel.onEvent(EditCurrencySymbolEvent.OnClickCancel) },
        header = { Text(text = stringResource(R.string.editCurrencySymbol_header)) },
        actionButtons = {
            AppDialogActionButton(
                onClick = { viewModel.onEvent(EditCurrencySymbolEvent.OnClickCancel) },
                enabled = !state.waiting,
                content = {
                    Text(text = stringResource(R.string.editCurrencySymbol_action_cancelSavingCurrencySymbol))
                }
            )
            AppDialogActionButton(
                onClick = { viewModel.onEvent(EditCurrencySymbolEvent.OnClickSave) },
                primaryButton = true,
                enabled = !state.waiting,
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
            value = state.symbolValue,
            onValueChange = {
                val event = EditCurrencySymbolEvent.OnSymbolChanged(it)
                viewModel.onEvent(event)
            },
            label = { Text(text = stringResource(R.string.editCurrencySymbol_label_symbol)) },
            trailingIcon = {
                IconButton(
                    onClick = {
                        val event = EditCurrencySymbolEvent.OnSymbolChanged(TextFieldValue())
                        viewModel.onEvent(event)
                    },
                    content = { ClearDataIcon() }
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.onEvent(EditCurrencySymbolEvent.OnClickSave) }
            )
        )
    }
}