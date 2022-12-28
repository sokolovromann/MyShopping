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
import ru.sokolovromann.myshopping.ui.compose.event.EditTaxRateScreenEvent
import ru.sokolovromann.myshopping.ui.utils.toTextField
import ru.sokolovromann.myshopping.ui.viewmodel.EditTaxRateViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditTaxRateEvent

@Composable
fun EditTaxRateScreen(
    navController: NavController,
    viewModel: EditTaxRateViewModel = hiltViewModel()
) {
    val screenData = viewModel.editTaxRateState.screenData
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                EditTaxRateScreenEvent.ShowBackScreen -> {
                    focusManager.clearFocus(force = true)
                    navController.popBackStack()
                }

                EditTaxRateScreenEvent.ShowKeyboard -> {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    AppDialog(
        onDismissRequest = { viewModel.onEvent(EditTaxRateEvent.CancelSavingTaxRate) },
        header = { Text(text = stringResource(R.string.editTaxRate_header)) },
        actionButtons = {
            AppDialogActionButton(
                onClick = { viewModel.onEvent(EditTaxRateEvent.CancelSavingTaxRate) },
                content = {
                    Text(text = stringResource(R.string.editTaxRate_action_cancelSavingTaxRate))
                }
            )
            AppDialogActionButton(
                onClick = { viewModel.onEvent(EditTaxRateEvent.SaveTaxRate) },
                primaryButton = true,
                content = {
                    Text(text = stringResource(R.string.editTaxRate_action_saveTaxRate))
                }
            )
        }
    ) {
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = screenData.taxRateValue,
            valueFontSize = screenData.fontSize.toTextField().sp,
            onValueChange = {
                val event = EditTaxRateEvent.TaxRateChanged(it)
                viewModel.onEvent(event)
            },
            label = { Text(text = stringResource(R.string.editTaxRate_label_taxRate)) },
            error = { Text(text = stringResource(R.string.editTaxRate_message_taxRateError)) },
            showError = screenData.showTaxRateError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.onEvent(EditTaxRateEvent.SaveTaxRate) }
            )
        )
    }
}