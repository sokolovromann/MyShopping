package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import ru.sokolovromann.myshopping.ui.compose.event.EditTaxRateScreenEvent
import ru.sokolovromann.myshopping.ui.model.UiIcon
import ru.sokolovromann.myshopping.ui.utils.updateProductsWidgets
import ru.sokolovromann.myshopping.ui.viewmodel.EditTaxRateViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditTaxRateEvent

@Composable
fun EditTaxRateScreen(
    navController: NavController,
    viewModel: EditTaxRateViewModel = hiltViewModel()
) {
    val state = viewModel.editTaxRateState
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                EditTaxRateScreenEvent.OnShowBackScreen -> {
                    updateProductsWidgets(context)
                    focusManager.clearFocus(force = true)
                    navController.popBackStack()
                }

                EditTaxRateScreenEvent.OnShowKeyboard -> {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    DefaultDialog(
        onDismissRequest = {
            val event = EditTaxRateEvent.OnClickCancel
            viewModel.onEvent(event)
        },
        header = { Text(stringResource(R.string.editTaxRate_header)) },
        actionButtons = {
            TextButton(
                onClick = { viewModel.onEvent(EditTaxRateEvent.OnClickCancel) },
                enabled = !state.waiting,
                content = { Text(stringResource(R.string.editTaxRate_action_cancelSavingTaxRate)) }
            )
            TextButton(
                onClick = { viewModel.onEvent(EditTaxRateEvent.OnClickSave) },
                enabled = !state.waiting,
                content = { Text(stringResource(R.string.editTaxRate_action_saveTaxRate)) }
            )
        }
    ) {
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = state.taxRateValue,
            onValueChange = {
                val event = EditTaxRateEvent.OnTaxRateChanged(it)
                viewModel.onEvent(event)
            },
            label = { Text(text = stringResource(R.string.editTaxRate_label_taxRate)) },
            trailingIcon = {
                IconButton(
                    onClick = {
                        val event = EditTaxRateEvent.OnTaxRateChanged(TextFieldValue())
                        viewModel.onEvent(event)
                    }
                ) {
                    DefaultIcon(
                        icon = UiIcon.Clear
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.onEvent(EditTaxRateEvent.OnClickSave) }
            )
        )
    }
}