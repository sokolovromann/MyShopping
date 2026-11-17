package ru.sokolovromann.myshopping.feature.settings.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterEditingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductEnter
import ru.sokolovromann.myshopping.core.domain.model.Tax
import ru.sokolovromann.myshopping.core.ui.component.DialogContent
import ru.sokolovromann.myshopping.core.ui.component.DialogTitle
import ru.sokolovromann.myshopping.core.ui.component.DropdownMenuSelectableItem
import ru.sokolovromann.myshopping.core.ui.component.DropdownMenuTitle
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.ui.action.AddEditProductSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.model.UiAddEditProductPreferences
import ru.sokolovromann.myshopping.feature.settings.ui.model.UiPreference

@Composable
fun SettingsAddEditProductCard(
    onAction: (AddEditProductSettingsAction) -> Unit,
    preferences: UiAddEditProductPreferences
) {
    SettingsCard(stringResource(R.string.settings_text_add_edit_product)) {
        AfterTappingEnterItem(preferences.afterTappingEnter) {
            val action = AddEditProductSettingsAction.OnAfterTappingEnterSelected(it)
            onAction(action)
        }
        AfterAddingItem(preferences.afterAdding) {
            val action = AddEditProductSettingsAction.OnAfterAddingSelected(it)
            onAction(action)
        }
        AfterEditingItem(preferences.afterEditing) {
            val action = AddEditProductSettingsAction.OnAfterEditingSelected(it)
            onAction(action)
        }
        TaxItem(preferences.tax) {
            val action = AddEditProductSettingsAction.OnTaxSaved(it)
            onAction(action)
        }
    }
}

// AFTER TAPPING ENTER

@Composable
private fun AfterTappingEnterItem(
    data: UiPreference<AfterTappingByProductEnter>,
    onSelected: (AfterTappingByProductEnter) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_after_tapping_enter)) },
        body = {
            Text(data.text.asCompose())
            AfterTappingEnterMenu(
                expanded = expanded,
                onSelected = { newAfterTappingEnter ->
                    expanded = false
                    newAfterTappingEnter?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun AfterTappingEnterMenu(
    expanded: Boolean,
    onSelected: (AfterTappingByProductEnter?) -> Unit,
    selected: AfterTappingByProductEnter
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_after_tapping_enter))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_tapping_enter_save),
            selected = selected == AfterTappingByProductEnter.SaveProduct,
            onClick = { onSelected(AfterTappingByProductEnter.SaveProduct) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_tapping_enter_next_field),
            selected = selected == AfterTappingByProductEnter.GoToNextField,
            onClick = { onSelected(AfterTappingByProductEnter.GoToNextField) }
        )
    }
}

// AFTER ADDING

@Composable
private fun AfterAddingItem(
    data: UiPreference<AfterAddingProduct>,
    onSelected: (AfterAddingProduct) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_after_adding_product)) },
        body = {
            Text(data.text.asCompose())
            AfterAddingMenu(
                expanded = expanded,
                onSelected = { newAfterAdding ->
                    expanded = false
                    newAfterAdding?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun AfterAddingMenu(
    expanded: Boolean,
    onSelected: (AfterAddingProduct?) -> Unit,
    selected: AfterAddingProduct
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_after_adding_product))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_adding_product_close),
            selected = selected == AfterAddingProduct.CloseScreen,
            onClick = { onSelected(AfterAddingProduct.CloseScreen) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_adding_product_open),
            selected = selected == AfterAddingProduct.OpenNewScreen,
            onClick = { onSelected(AfterAddingProduct.OpenNewScreen) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_adding_product_do_nothing),
            selected = selected == AfterAddingProduct.DoNothing,
            onClick = { onSelected(AfterAddingProduct.DoNothing) }
        )
    }
}

// AFTER EDITING

@Composable
private fun AfterEditingItem(
    data: UiPreference<AfterEditingProduct>,
    onSelected: (AfterEditingProduct) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_after_editing_product)) },
        body = {
            Text(data.text.asCompose())
            AfterEditingMenu(
                expanded = expanded,
                onSelected = { newAfterEditing ->
                    expanded = false
                    newAfterEditing?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun AfterEditingMenu(
    expanded: Boolean,
    onSelected: (AfterEditingProduct?) -> Unit,
    selected: AfterEditingProduct
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_after_editing_product))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_editing_product_close),
            selected = selected == AfterEditingProduct.CloseScreen,
            onClick = { onSelected(AfterEditingProduct.CloseScreen) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_editing_product_next),
            selected = selected == AfterEditingProduct.OpenNextProductScreen,
            onClick = { onSelected(AfterEditingProduct.OpenNextProductScreen) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_editing_product_do_nothing),
            selected = selected == AfterEditingProduct.DoNothing,
            onClick = { onSelected(AfterEditingProduct.DoNothing) }
        )
    }
}

// TAX

@Composable
private fun TaxItem(
    data: UiPreference<String>,
    onSaved: (Tax) -> Unit
) {
    var displayDialog by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_tax)) },
        body = {
            Text(data.text.asCompose())
            TaxDialog(
                display = displayDialog,
                onSaved = { newTax ->
                    displayDialog = false
                    newTax?.let { onSaved(it) }
                },
                data = data.selected
            )
        },
        onClick = { displayDialog = true }
    )
}

@Composable
private fun TaxDialog(
    display: Boolean,
    onSaved: (Tax?) -> Unit,
    data: String
) {
    if (!display) return

    var taxValue by rememberSaveable { mutableStateOf(data) }

    AlertDialog(
        onDismissRequest = { onSaved(null) },
        confirmButton = {
            TextButton(
                onClick = {
                    val newTax = Tax(taxValue.toBigDecimal())
                    onSaved(newTax)
                },
                content = { Text(stringResource(R.string.settings_action_tax_save)) }
            )
        },
        dismissButton = {
            TextButton(
                onClick = { onSaved(null) },
                content = { Text(stringResource(R.string.settings_action_tax_cancel)) }
            )
        },
        title = { DialogTitle(stringResource(R.string.settings_text_tax)) },
        text = {
            DialogContent {
                OutlinedTextField(
                    value = taxValue,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*[.,]?\\d*$"))) {
                            taxValue = newValue.replace(",", ".")
                        }
                    },
                    label = { Text(stringResource(R.string.settings_label_tax)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}