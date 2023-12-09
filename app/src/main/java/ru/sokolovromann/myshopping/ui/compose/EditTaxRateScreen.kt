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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.EditTaxRateScreenEvent
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

    AppDialog(
        onDismissRequest = { viewModel.onEvent(EditTaxRateEvent.OnClickCancel) },
        header = { Text(text = stringResource(R.string.editTaxRate_header)) },
        actionButtons = {
            AppDialogActionButton(
                onClick = { viewModel.onEvent(EditTaxRateEvent.OnClickCancel) },
                enabled = !state.waiting,
                content = { Text(text = stringResource(R.string.editTaxRate_action_cancelSavingTaxRate)) }
            )
            AppDialogActionButton(
                onClick = { viewModel.onEvent(EditTaxRateEvent.OnClickSave) },
                primaryButton = true,
                enabled = !state.waiting,
                content = { Text(text = stringResource(R.string.editTaxRate_action_saveTaxRate)) }
            )
        }
    ) {
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = state.taxRateValue,
            valueFontSize = state.fontSize.textField.sp,
            onValueChange = {
                val event = EditTaxRateEvent.OnTaxRateChanged(it)
                viewModel.onEvent(event)
            },
            label = { Text(text = stringResource(R.string.editTaxRate_label_taxRate)) },
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