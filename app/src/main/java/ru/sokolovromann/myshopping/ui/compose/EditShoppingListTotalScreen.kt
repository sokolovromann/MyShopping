package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.EditShoppingListTotalScreenEvent
import ru.sokolovromann.myshopping.ui.utils.toTextField
import ru.sokolovromann.myshopping.ui.utils.updateProductsWidget
import ru.sokolovromann.myshopping.ui.viewmodel.EditShoppingListTotalViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditShoppingListTotalEvent

@Composable
fun EditShoppingListTotalScreen(
    navController: NavController,
    viewModel: EditShoppingListTotalViewModel = hiltViewModel()
) {

    val screenData = viewModel.editShoppingListTotalState.screenData
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                EditShoppingListTotalScreenEvent.ShowBackScreen -> {
                    focusManager.clearFocus(force = true)
                    navController.popBackStack()
                }

                is EditShoppingListTotalScreenEvent.ShowBackScreenAndUpdateProductsWidget -> {
                    updateProductsWidget(context, it.shoppingUid)
                    focusManager.clearFocus(force = true)
                    navController.popBackStack()
                }

                EditShoppingListTotalScreenEvent.ShowKeyboard -> {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    AppDialog(
        onDismissRequest = {
            viewModel.onEvent(EditShoppingListTotalEvent.CancelSavingShoppingListTotal)
        },
        header = { Text(text = screenData.headerText.asCompose()) },
        actionButtons = {
            AppDialogActionButton(
                onClick = { viewModel.onEvent(EditShoppingListTotalEvent.CancelSavingShoppingListTotal) },
                content = {
                    Text(text = stringResource(R.string.editShoppingListTotal_action_cancelSavingShoppingListTotal))
                }
            )
            AppDialogActionButton(
                onClick = { viewModel.onEvent(EditShoppingListTotalEvent.SaveShoppingListTotal) },
                primaryButton = true,
                content = {
                    Text(text = stringResource(R.string.editShoppingListTotal_action_saveShoppingListTotal))
                }
            )
        }
    ) {
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = screenData.totalValue,
            valueFontSize = screenData.fontSize.toTextField().sp,
            onValueChange = {
                val event = EditShoppingListTotalEvent.ShoppingListTotalChanged(it)
                viewModel.onEvent(event)
            },
            label = { Text(text = stringResource(R.string.editShoppingListTotal_label_total)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { viewModel.onEvent(EditShoppingListTotalEvent.SaveShoppingListTotal) }
            )
        )

        Spacer(modifier = Modifier.size(EditShoppingListTotalSpacerSmallSize))

        Text(
            text = stringResource(R.string.editShoppingListTotal_message_totalWarning),
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onSurface
            )
        )
    }
}

private val EditShoppingListTotalSpacerSmallSize = 8.dp