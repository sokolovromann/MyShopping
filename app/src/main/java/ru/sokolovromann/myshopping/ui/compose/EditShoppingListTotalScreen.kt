package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.ui.compose.event.EditShoppingListTotalScreenEvent
import ru.sokolovromann.myshopping.ui.model.UiIcon
import ru.sokolovromann.myshopping.ui.utils.updateProductsWidget
import ru.sokolovromann.myshopping.ui.viewmodel.EditShoppingListTotalViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.EditShoppingListTotalEvent

@Composable
fun EditShoppingListTotalScreen(
    navController: NavController,
    viewModel: EditShoppingListTotalViewModel = hiltViewModel()
) {

    val state = viewModel.editShoppingListTotalState
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                is EditShoppingListTotalScreenEvent.OnShowBackScreen -> {
                    updateProductsWidget(context, it.shoppingUid)
                    focusManager.clearFocus(force = true)
                    navController.popBackStack()
                }

                EditShoppingListTotalScreenEvent.OnShowKeyboard -> {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    DefaultDialog(
        onDismissRequest = {
            val event = EditShoppingListTotalEvent.OnClickCancel
            viewModel.onEvent(event)
        },
        header = { Text(state.header.asCompose()) },
        actionButtons = {
            TextButton(
                onClick = { viewModel.onEvent(EditShoppingListTotalEvent.OnClickCancel) },
                enabled = !state.waiting,
                content = { Text(stringResource(R.string.editShoppingListTotal_action_cancelSavingShoppingListTotal)) }
            )
            TextButton(
                onClick = { viewModel.onEvent(EditShoppingListTotalEvent.OnClickSave) },
                enabled = !state.waiting,
                content = { Text(stringResource(R.string.editShoppingListTotal_action_saveShoppingListTotal)) }
            )
        }
    ) {
        OutlinedAppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = state.totalValue,
            onValueChange = {
                val event = EditShoppingListTotalEvent.OnTotalChanged(it)
                viewModel.onEvent(event)
            },
            label = { Text(text = stringResource(R.string.editShoppingListTotal_label_total)) },
            trailingIcon = {
                IconButton(
                    onClick = {
                        val event = EditShoppingListTotalEvent.OnTotalChanged(TextFieldValue())
                        viewModel.onEvent(event)
                    },
                    content = { DefaultIcon(UiIcon.Clear) }
                )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) },
            )
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(EditShoppingListTotalPaddings)
        ) {
            OutlinedAppTextField(
                modifier = Modifier.weight(0.5f),
                value = state.discountValue,
                onValueChange = {
                    val event = EditShoppingListTotalEvent.OnDiscountChanged(it)
                    viewModel.onEvent(event)
                },
                label = { Text(text = stringResource(R.string.editShoppingListTotal_label_discount)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Next) }
                )
            )

            Spacer(modifier = Modifier.size(EditShoppingListTotalSpacerSize))

            EditShoppingListSelectButton(
                modifier = Modifier.weight(0.5f),
                onClick = { viewModel.onEvent(EditShoppingListTotalEvent.OnSelectDiscountAsPercent(true)) },
            ) {
                Text(
                    text = state.discountAsPercentValue.text.asCompose(),
                    color = MaterialTheme.colors.onSurface
                )

                AppDropdownMenu(
                    expanded = state.expandedDiscountAsPercent,
                    onDismissRequest = { viewModel.onEvent(EditShoppingListTotalEvent.OnSelectDiscountAsPercent(false)) }
                ) {
                    AppDropdownMenuItem(
                        onClick = { viewModel.onEvent(EditShoppingListTotalEvent.OnDiscountAsPercentSelected(true)) },
                        text = { Text(text = stringResource(R.string.editShoppingListTotal_action_selectDiscountAsPercents)) },
                        right = { CheckmarkAppCheckbox(checked = state.discountAsPercentValue.selected) }
                    )
                    AppDropdownMenuItem(
                        onClick = { viewModel.onEvent(EditShoppingListTotalEvent.OnDiscountAsPercentSelected(false)) },
                        text = { Text(text = stringResource(R.string.editShoppingListTotal_action_selectDiscountAsMoney)) },
                        right = { CheckmarkAppCheckbox(checked = !state.discountAsPercentValue.selected) }
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(EditShoppingListTotalPaddings)
        ) {
            OutlinedAppTextField(
                modifier = Modifier.weight(0.5f),
                value = state.budgetValue,
                onValueChange = {
                    val event = EditShoppingListTotalEvent.OnBudgetChanged(it)
                    viewModel.onEvent(event)
                },
                label = { Text(text = stringResource(R.string.editShoppingListTotal_label_budget)) },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { viewModel.onEvent(EditShoppingListTotalEvent.OnClickSave) }
                )
            )

            Spacer(modifier = Modifier.size(EditShoppingListTotalSpacerSize))

            EditShoppingListSelectButton(
                modifier = Modifier.weight(0.5f),
                onClick = { viewModel.onEvent(EditShoppingListTotalEvent.OnSelectBudgetProducts(true)) },
            ) {
                Text(
                    text = state.budgetProducts.text.asCompose(),
                    color = MaterialTheme.colors.onSurface
                )

                AppDropdownMenu(
                    expanded = state.expandedBudgetProducts,
                    onDismissRequest = { viewModel.onEvent(EditShoppingListTotalEvent.OnSelectBudgetProducts(false)) }
                ) {
                    AppDropdownMenuItem(
                        onClick = { viewModel.onEvent(EditShoppingListTotalEvent.OnBudgetProductsSelected(DisplayTotal.ALL)) },
                        text = { Text(text = stringResource(R.string.editShoppingListTotal_action_selectAllProducts)) },
                        right = { CheckmarkAppCheckbox(checked = state.budgetProducts.selected == DisplayTotal.ALL) }
                    )
                    AppDropdownMenuItem(
                        onClick = { viewModel.onEvent(EditShoppingListTotalEvent.OnBudgetProductsSelected(DisplayTotal.COMPLETED)) },
                        text = { Text(text = stringResource(R.string.editShoppingListTotal_action_selectCompletedProducts)) },
                        right = { CheckmarkAppCheckbox(checked = state.budgetProducts.selected == DisplayTotal.COMPLETED) }
                    )
                    AppDropdownMenuItem(
                        onClick = { viewModel.onEvent(EditShoppingListTotalEvent.OnBudgetProductsSelected(DisplayTotal.ACTIVE)) },
                        text = { Text(text = stringResource(R.string.editShoppingListTotal_action_selectActiveProducts)) },
                        right = { CheckmarkAppCheckbox(checked = state.budgetProducts.selected == DisplayTotal.ACTIVE) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EditShoppingListSelectButton(
    modifier: Modifier,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedButton(
        modifier = Modifier
            .defaultMinSize(minHeight = EditShoppingListTotalButtonHeight)
            .padding(EditShoppingListTotalButtonPaddings)
            .then(modifier),
        onClick = onClick,
        contentPadding = EditShoppingListTotalButtonNoContentPadding,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            content = content
        )
    }
}

private val EditShoppingListTotalSpacerSize = 8.dp
private val EditShoppingListTotalPaddings = PaddingValues(vertical = 2.dp)
private val EditShoppingListTotalButtonHeight = 64.dp
private val EditShoppingListTotalButtonPaddings = PaddingValues(top = 8.dp)
private val EditShoppingListTotalButtonNoContentPadding = PaddingValues(0.dp)