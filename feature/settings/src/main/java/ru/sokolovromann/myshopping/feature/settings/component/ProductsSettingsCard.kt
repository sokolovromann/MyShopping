package ru.sokolovromann.myshopping.feature.settings.component

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingProduct
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductCheckbox
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByProductItem
import ru.sokolovromann.myshopping.core.domain.model.CheckboxColor
import ru.sokolovromann.myshopping.core.domain.model.GroupProductsByStatus
import ru.sokolovromann.myshopping.core.domain.model.ProductsAddingMode
import ru.sokolovromann.myshopping.core.domain.model.StrikethroughCompletedProducts
import ru.sokolovromann.myshopping.core.domain.model.SwipeProduct
import ru.sokolovromann.myshopping.core.domain.model.SwipeProductActionName
import ru.sokolovromann.myshopping.core.ui.component.DropdownMenuSelectableItem
import ru.sokolovromann.myshopping.core.ui.component.DropdownMenuTitle
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.action.ProductsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.model.ProductsSettingsItems
import ru.sokolovromann.myshopping.feature.settings.model.SettingsItem

@Composable
fun ProductsSettingsCard(
    onAction: (ProductsSettingsAction) -> Unit,
    items: ProductsSettingsItems
) {
    SettingsCard(stringResource(R.string.settings_text_products)) {
        AddingModeItem(items.addingMode) {
            val action = ProductsSettingsAction.OnAddingModeSelected(it)
            onAction(action)
        }
        StrikethroughCompletedItem(items.strikethroughCompleted) {
            val action = ProductsSettingsAction.OnStrikethroughCompletedSelected(it)
            onAction(action)
        }
        AfterCompletingItem(items.afterCompleting) {
            val action = ProductsSettingsAction.OnAfterCompletingSelected(it)
            onAction(action)
        }
        AfterTappingCheckboxItem(items.afterTappingCheckbox) {
            val action = ProductsSettingsAction.OnAfterTappingCheckboxSelected(it)
            onAction(action)
        }
        CheckboxColorItem(items.checkboxColor) {
            val action = ProductsSettingsAction.OnCheckboxColorSelected(it)
            onAction(action)
        }
        AfterTappingProductItemItem(items.afterTappingItem) {
            val action = ProductsSettingsAction.OnAfterTappingItemSelected(it)
            onAction(action)
        }
        SwipeProductLeftItem(items.swipeLeft) {
            val action = ProductsSettingsAction.OnSwipeLeftSelected(it)
            onAction(action)
        }
        SwipeProductRightItem(items.swipeRight) {
            val action = ProductsSettingsAction.OnSwipeRightSelected(it)
            onAction(action)
        }
    }
}

// GROUP

@Composable
fun GroupProductsItem(
    data: SettingsItem<GroupProductsByStatus>,
    onSelected: (GroupProductsByStatus) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_group_products_by_status)) },
        body = {
            Text(data.text.asCompose())
            GroupProductsMenu(
                expanded = expanded,
                onSelected = { newGroup ->
                    expanded = false
                    newGroup?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun GroupProductsMenu(
    expanded: Boolean,
    onSelected: (GroupProductsByStatus?) -> Unit,
    selected: GroupProductsByStatus
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_group_products_by_status))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_group_products_by_status_completed_first),
            selected = selected == GroupProductsByStatus.CompletedFirst,
            onClick = { onSelected(GroupProductsByStatus.CompletedFirst) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_group_products_by_status_active_first),
            selected = selected == GroupProductsByStatus.ActiveFirst,
            onClick = { onSelected(GroupProductsByStatus.ActiveFirst) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_group_products_by_status_hide_completed),
            selected = selected == GroupProductsByStatus.HideCompleted,
            onClick = { onSelected(GroupProductsByStatus.HideCompleted) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_group_products_by_status_do_not_group),
            selected = selected == GroupProductsByStatus.DoNotGroup,
            onClick = { onSelected(GroupProductsByStatus.DoNotGroup) }
        )
    }
}

// ADDING MODE

@Composable
private fun AddingModeItem(
    data: SettingsItem<ProductsAddingMode>,
    onSelected: (ProductsAddingMode) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_products_adding_mode)) },
        body = {
            Text(data.text.asCompose())
            AddingModeMenu(
                expanded = expanded,
                onSelected = { newAddingMode ->
                    expanded = false
                    newAddingMode?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun AddingModeMenu(
    expanded: Boolean,
    onSelected: (ProductsAddingMode?) -> Unit,
    selected: ProductsAddingMode
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_products_adding_mode))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_products_adding_mode_simple),
            selected = selected == ProductsAddingMode.Simple,
            onClick = { onSelected(ProductsAddingMode.Simple) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_products_adding_mode_advanced),
            selected = selected == ProductsAddingMode.Advanced,
            onClick = { onSelected(ProductsAddingMode.Advanced) }
        )
    }
}

// STRIKETHROUGH COMPLETED

@Composable
private fun StrikethroughCompletedItem(
    data: SettingsItem<StrikethroughCompletedProducts>,
    onSelected: (StrikethroughCompletedProducts) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_strikethrough_products_completed)) },
        body = {
            Text(data.text.asCompose())
            StrikethroughCompletedMenu(
                expanded = expanded,
                onSelected = { newStrikethroughCompleted ->
                    expanded = false
                    newStrikethroughCompleted?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun StrikethroughCompletedMenu(
    expanded: Boolean,
    onSelected: (StrikethroughCompletedProducts?) -> Unit,
    selected: StrikethroughCompletedProducts
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_strikethrough_products_completed))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_strikethrough_products_completed_on),
            selected = selected == StrikethroughCompletedProducts.On,
            onClick = { onSelected(StrikethroughCompletedProducts.On) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_strikethrough_products_completed_off),
            selected = selected == StrikethroughCompletedProducts.Off,
            onClick = { onSelected(StrikethroughCompletedProducts.Off) }
        )
    }
}

// AFTER COMPLETING

@Composable
private fun AfterCompletingItem(
    data: SettingsItem<AfterCompletingProduct>,
    onSelected: (AfterCompletingProduct) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_after_completing_product)) },
        body = {
            Text(data.text.asCompose())
            AfterCompletingMenu(
                expanded = expanded,
                onSelected = { newAfterCompleting ->
                    expanded = false
                    newAfterCompleting?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun AfterCompletingMenu(
    expanded: Boolean,
    onSelected: (AfterCompletingProduct?) -> Unit,
    selected: AfterCompletingProduct
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_after_completing_product))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_completing_product_edit),
            selected = selected == AfterCompletingProduct.EditProduct,
            onClick = { onSelected(AfterCompletingProduct.EditProduct) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_completing_product_delete),
            selected = selected == AfterCompletingProduct.DeleteProduct,
            onClick = { onSelected(AfterCompletingProduct.DeleteProduct) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_completing_product_do_nothing),
            selected = selected == AfterCompletingProduct.DoNothing,
            onClick = { onSelected(AfterCompletingProduct.DoNothing) }
        )
    }
}

// AFTER TAPPING CHECKBOX

@Composable
private fun AfterTappingCheckboxItem(
    data: SettingsItem<AfterTappingByProductCheckbox>,
    onSelected: (AfterTappingByProductCheckbox) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_after_tapping_product_checkbox)) },
        body = {
            Text(data.text.asCompose())
            AfterTappingCheckboxMenu(
                expanded = expanded,
                onSelected = { newAfterTappingCheckbox ->
                    expanded = false
                    newAfterTappingCheckbox?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun AfterTappingCheckboxMenu(
    expanded: Boolean,
    onSelected: (AfterTappingByProductCheckbox?) -> Unit,
    selected: AfterTappingByProductCheckbox
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_after_tapping_product_checkbox))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_tapping_product_checkbox_change_status),
            selected = selected == AfterTappingByProductCheckbox.ChangeProductStatus,
            onClick = { onSelected(AfterTappingByProductCheckbox.ChangeProductStatus) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_tapping_product_checkbox_do_nothing),
            selected = selected == AfterTappingByProductCheckbox.DoNothing,
            onClick = { onSelected(AfterTappingByProductCheckbox.DoNothing) }
        )
    }
}

// CHECKBOX COLOR

@Composable
private fun CheckboxColorItem(
    data: SettingsItem<CheckboxColor>,
    onSelected: (CheckboxColor) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_product_checkbox_color)) },
        body = {
            Text(data.text.asCompose())
            CheckboxColorMenu(
                expanded = expanded,
                onSelected = { newCheckboxColor ->
                    expanded = false
                    newCheckboxColor?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun CheckboxColorMenu(
    expanded: Boolean,
    onSelected: (CheckboxColor?) -> Unit,
    selected: CheckboxColor
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_product_checkbox_color))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_product_checkbox_color_red_green),
            selected = selected == CheckboxColor.RedOrGreen,
            onClick = { onSelected(CheckboxColor.RedOrGreen) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_product_checkbox_color_red_grey),
            selected = selected == CheckboxColor.RedOrGray,
            onClick = { onSelected(CheckboxColor.RedOrGray) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_product_checkbox_color_grey),
            selected = selected == CheckboxColor.Gray,
            onClick = { onSelected(CheckboxColor.Gray) }
        )
    }
}

// AFTER TAPPING PRODUCT ITEM

@Composable
private fun AfterTappingProductItemItem(
    data: SettingsItem<AfterTappingByProductItem>,
    onSelected: (AfterTappingByProductItem) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_after_tapping_product_item)) },
        body = {
            Text(data.text.asCompose())
            AfterTappingProductItemMenu(
                expanded = expanded,
                onSelected = { newAfterTappingByProductItem ->
                    expanded = false
                    newAfterTappingByProductItem?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun AfterTappingProductItemMenu(
    expanded: Boolean,
    onSelected: (AfterTappingByProductItem?) -> Unit,
    selected: AfterTappingByProductItem
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_after_tapping_product_item))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_tapping_product_item_change_status),
            selected = selected == AfterTappingByProductItem.ChangeProductStatus,
            onClick = { onSelected(AfterTappingByProductItem.ChangeProductStatus) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_tapping_product_item_do_nothing),
            selected = selected == AfterTappingByProductItem.DoNothing,
            onClick = { onSelected(AfterTappingByProductItem.DoNothing) }
        )
    }
}

// SWIPE

@Composable
private fun SwipeProductLeftItem(
    data: SettingsItem<SwipeProduct.Left>,
    onSelected: (SwipeProduct.Left) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_swipe_product_left)) },
        body = {
            Text(data.text.asCompose())
            SwipeProductMenu(
                expanded = expanded,
                title = stringResource(R.string.settings_text_swipe_product_left),
                onSelected = { newAfterTappingByProductItem ->
                    expanded = false
                    newAfterTappingByProductItem?.let { onSelected(SwipeProduct.Left(it)) }
                },
                selected = data.selected.actionName
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun SwipeProductRightItem(
    data: SettingsItem<SwipeProduct.Right>,
    onSelected: (SwipeProduct.Right) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_swipe_product_right)) },
        body = {
            Text(data.text.asCompose())
            SwipeProductMenu(
                expanded = expanded,
                title = stringResource(R.string.settings_text_swipe_product_right),
                onSelected = { newAfterTappingByProductItem ->
                    expanded = false
                    newAfterTappingByProductItem?.let { onSelected(SwipeProduct.Right(it)) }
                },
                selected = data.selected.actionName
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun SwipeProductMenu(
    expanded: Boolean,
    title: String,
    onSelected: (SwipeProductActionName?) -> Unit,
    selected: SwipeProductActionName
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(title)

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_swipe_product_edit),
            selected = selected == SwipeProductActionName.EditProduct,
            onClick = { onSelected(SwipeProductActionName.EditProduct) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_swipe_product_delete),
            selected = selected == SwipeProductActionName.DeleteProduct,
            onClick = { onSelected(SwipeProductActionName.DeleteProduct) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_swipe_product_change_status),
            selected = selected == SwipeProductActionName.ChangeProductStatusToCompletedOrActive,
            onClick = { onSelected(SwipeProductActionName.ChangeProductStatusToCompletedOrActive) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_swipe_product_off),
            selected = selected == SwipeProductActionName.Off,
            onClick = { onSelected(SwipeProductActionName.Off) }
        )
    }
}