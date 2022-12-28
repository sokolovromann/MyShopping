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
import ru.sokolovromann.myshopping.ui.compose.event.EditShoppingListNameScreenEvent
import ru.sokolovromann.myshopping.ui.utils.toTextField
import ru.sokolovromann.myshopping.ui.viewmodel.EditShoppingListNameViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditShoppingListNameEvent

@Composable
fun EditShoppingListNameScreen(
    navController: NavController,
    viewModel: EditShoppingListNameViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                EditShoppingListNameScreenEvent.ShowBackScreen -> navController.popBackStack()
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
        onDismissRequest = {
            viewModel.onEvent(EditShoppingListNameEvent.CancelSavingShoppingListName)
        },
        header = { Text(text = viewModel.editShoppingListNameState.screenData.headerText.asCompose()) },
        actionButtons = { ActionButtons(viewModel) },
        content = { Content(viewModel, focusRequester) }
    )
}

@Composable
private fun ActionButtons(viewModel: EditShoppingListNameViewModel) {
    AppDialogActionButton(
        onClick = { viewModel.onEvent(EditShoppingListNameEvent.CancelSavingShoppingListName) },
        content = { Text(text = viewModel.cancelState.value.text.asCompose()) }
    )
    AppDialogActionButton(
        onClick = { viewModel.onEvent(EditShoppingListNameEvent.SaveShoppingListName) },
        primaryButton = true,
        content = { Text(text = viewModel.saveState.value.text.asCompose()) }
    )
}

@Composable
private fun Content(viewModel: EditShoppingListNameViewModel, focusRequester: FocusRequester) {
    val screenData = viewModel.editShoppingListNameState.screenData
    OutlinedAppTextField(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = screenData.nameValue,
        valueFontSize = screenData.fontSize.toTextField().sp,
        onValueChange = {
            val event = EditShoppingListNameEvent.ShoppingListNameChanged(it)
            viewModel.onEvent(event)
        },
        label = { Text(text = stringResource(R.string.editShoppingListName_label_name)) },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { viewModel.onEvent(EditShoppingListNameEvent.SaveShoppingListName) }
        )
    )
}