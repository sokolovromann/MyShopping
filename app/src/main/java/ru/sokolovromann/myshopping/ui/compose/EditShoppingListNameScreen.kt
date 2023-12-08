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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.EditShoppingListNameScreenEvent
import ru.sokolovromann.myshopping.ui.utils.updateProductsWidget
import ru.sokolovromann.myshopping.ui.viewmodel.EditShoppingListNameViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditShoppingListNameEvent

@Composable
fun EditShoppingListNameScreen(
    navController: NavController,
    viewModel: EditShoppingListNameViewModel = hiltViewModel()
) {
    val state = viewModel.editShoppingListNameState
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                is EditShoppingListNameScreenEvent.OnShowBackScreen -> {
                    updateProductsWidget(context, it.shoppingUid)
                    focusManager.clearFocus(force = true)
                    navController.popBackStack()
                }

                EditShoppingListNameScreenEvent.OnShowKeyboard -> {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    AppDialog(
        onDismissRequest = { viewModel.onEvent(EditShoppingListNameEvent.OnClickCancel) },
        header = { Text(text = state.header.asCompose()) },
        actionButtons = {
            AppDialogActionButton(
                onClick = { viewModel.onEvent(EditShoppingListNameEvent.OnClickCancel) },
                enabled = !state.waiting,
                content = { Text(text = stringResource(R.string.editShoppingListName_action_cancelSavingShoppingListName)) }
            )
            AppDialogActionButton(
                onClick = { viewModel.onEvent(EditShoppingListNameEvent.OnClickSave) },
                primaryButton = true,
                enabled = !state.waiting,
                content = { Text(text = stringResource(R.string.editShoppingListName_action_saveShoppingListName)) }
            )
        }
    ) {
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = state.nameValue,
            valueFontSize = state.fontSize.textField.sp,
            onValueChange = {
                val event = EditShoppingListNameEvent.OnNameChanged(it)
                viewModel.onEvent(event)
            },
            label = { Text(text = stringResource(R.string.editShoppingListName_label_name)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.onEvent(EditShoppingListNameEvent.OnClickSave) }
            )
        )
    }
}