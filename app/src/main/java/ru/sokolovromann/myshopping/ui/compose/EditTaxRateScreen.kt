package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
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
import ru.sokolovromann.myshopping.ui.compose.event.EditTaxRateScreenEvent
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
        title = { Title(viewModel) },
        actionButtons = { ActionButtons(viewModel) },
        content = { Content(viewModel, focusRequester) }
    )
}

@Composable
private fun Title(viewModel: EditTaxRateViewModel) {
    AppText(data = viewModel.headerState.value)
}

@Composable
private fun ActionButtons(viewModel: EditTaxRateViewModel) {
    TextButton(
        onClick = { viewModel.onEvent(EditTaxRateEvent.CancelSavingTaxRate) },
        content = { AppText(data = viewModel.cancelState.value) }
    )
    Spacer(modifier = Modifier.size(4.dp))
    OutlinedButton(
        onClick = { viewModel.onEvent(EditTaxRateEvent.SaveTaxRate) },
        content = { AppText(data = viewModel.saveState.value) }
    )
}

@Composable
private fun Content(viewModel: EditTaxRateViewModel, focusRequester: FocusRequester) {
    OutlinedAppTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        state = viewModel.taxRateState,
        onValueChange = {
            val event = EditTaxRateEvent.TaxRateChanged(it)
            viewModel.onEvent(event)
        }
    )
}