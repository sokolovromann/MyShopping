package ru.sokolovromann.myshopping.feature.settings.component

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import ru.sokolovromann.myshopping.core.domain.model.AfterAddingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterArchivingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingCart
import ru.sokolovromann.myshopping.core.domain.model.AfterTappingByCartCheckbox
import ru.sokolovromann.myshopping.core.domain.model.CheckboxColor
import ru.sokolovromann.myshopping.core.domain.model.DeletionCartFromTrash
import ru.sokolovromann.myshopping.core.domain.model.SwipeCart
import ru.sokolovromann.myshopping.core.domain.model.SwipeCartActionName
import ru.sokolovromann.myshopping.core.ui.component.DropdownMenuSelectableItem
import ru.sokolovromann.myshopping.core.ui.component.DropdownMenuTitle
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.action.CartsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.model.CartsSettingsItems
import ru.sokolovromann.myshopping.feature.settings.model.SettingsItem

@Composable
fun CartsSettingsCard(
    onAction: (CartsSettingsAction) -> Unit,
    items: CartsSettingsItems
) {
    SettingsCard(stringResource(R.string.settings_text_carts)) {
        AfterAddingItem(items.afterAdding) {
            val action = CartsSettingsAction.OnAfterAddingSelected(it)
            onAction(action)
        }
        AfterCompletingItem(items.afterCompleting) {
            val action = CartsSettingsAction.OnAfterCompletingSelected(it)
            onAction(action)
        }
        AfterArchivingItem(items.afterArchiving) {
            val action = CartsSettingsAction.OnAfterArchivingSelected(it)
            onAction(action)
        }
        AfterTappingCheckboxItem(items.afterTappingCheckbox) {
            val action = CartsSettingsAction.OnTappingCheckboxSelected(it)
            onAction(action)
        }
        CheckboxColorItem(items.checkboxColor) {
            val action = CartsSettingsAction.OnCheckboxColorSelected(it)
            onAction(action)
        }
        SwipeCartLeftItem(items.swipeLeft) {
            val action = CartsSettingsAction.OnSwipeLeftSelected(it)
            onAction(action)
        }
        SwipeCartRightItem(items.swipeRight) {
            val action = CartsSettingsAction.OnSwipeRightSelected(it)
            onAction(action)
        }
        DeletionFromTrashItem(items.deletionFromTrash) {
            val action = CartsSettingsAction.OnDeletionFromTrashSelected(it)
            onAction(action)
        }
    }
}

// AFTER ADDING

@Composable
private fun AfterAddingItem(
    data: SettingsItem<AfterAddingCart>,
    onSelected: (AfterAddingCart) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_after_adding_cart)) },
        body = {
            Text(data.text.asCompose())
            AfterAddingDropdownMenu(
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
private fun AfterAddingDropdownMenu(
    expanded: Boolean,
    onSelected: (AfterAddingCart?) -> Unit,
    selected: AfterAddingCart
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_after_adding_cart))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_adding_cart_open_products),
            selected = selected == AfterAddingCart.OpenProductsScreen,
            onClick = { onSelected(AfterAddingCart.OpenProductsScreen) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_adding_cart_open_add_name),
            selected = selected == AfterAddingCart.OpenAddNameScreen,
            onClick = { onSelected(AfterAddingCart.OpenAddNameScreen) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_adding_cart_open_add_product),
            selected = selected == AfterAddingCart.OpenAddProductScreen,
            onClick = { onSelected(AfterAddingCart.OpenAddProductScreen) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_adding_cart_do_nothing),
            selected = selected == AfterAddingCart.DoNothing,
            onClick = { onSelected(AfterAddingCart.DoNothing) }
        )
    }
}

// AFTER COMPLETING

@Composable
private fun AfterCompletingItem(
    data: SettingsItem<AfterCompletingCart>,
    onSelected: (AfterCompletingCart) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_after_completing_cart)) },
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
    onSelected: (AfterCompletingCart?) -> Unit,
    selected: AfterCompletingCart
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_after_completing_cart))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_completing_cart_archive_cart),
            selected = selected == AfterCompletingCart.ArchiveCart,
            onClick = { onSelected(AfterCompletingCart.ArchiveCart) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_completing_cart_delete_cart),
            selected = selected == AfterCompletingCart.DeleteCart,
            onClick = { onSelected(AfterCompletingCart.DeleteCart) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_completing_cart_delete_products),
            selected = selected == AfterCompletingCart.DeleteProducts,
            onClick = { onSelected(AfterCompletingCart.DeleteProducts) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_completing_cart_delete_cart_products),
            selected = selected == AfterCompletingCart.DeleteCartAndProducts,
            onClick = { onSelected(AfterCompletingCart.DeleteCartAndProducts) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_completing_cart_do_nothing),
            selected = selected == AfterCompletingCart.DoNothing,
            onClick = { onSelected(AfterCompletingCart.DoNothing) }
        )
    }
}

// AFTER ARCHIVING

@Composable
private fun AfterArchivingItem(
    data: SettingsItem<AfterArchivingCart>,
    onSelected: (AfterArchivingCart) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_after_archiving_cart)) },
        body = {
            Text(data.text.asCompose())
            AfterArchivingMenu(
                expanded = expanded,
                onSelected = { newAfterArchiving ->
                    expanded = false
                    newAfterArchiving?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun AfterArchivingMenu(
    expanded: Boolean,
    onSelected: (AfterArchivingCart?) -> Unit,
    selected: AfterArchivingCart
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_after_archiving_cart))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_archiving_cart_change_to_completed),
            selected = selected == AfterArchivingCart.ChangeCartStatusToCompleted,
            onClick = { onSelected(AfterArchivingCart.ChangeCartStatusToCompleted) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_archiving_cart_do_nothing),
            selected = selected == AfterArchivingCart.DoNothing,
            onClick = { onSelected(AfterArchivingCart.DoNothing) }
        )
    }
}

// AFTER TAPPING CHECKBOX

@Composable
private fun AfterTappingCheckboxItem(
    data: SettingsItem<AfterTappingByCartCheckbox>,
    onSelected: (AfterTappingByCartCheckbox) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_after_tapping_cart_checkbox)) },
        body = {
            Text(data.text.asCompose())
            AfterTappingCheckboxMenu(
                expanded = expanded,
                onSelected = { newAfterTapping ->
                    expanded = false
                    newAfterTapping?.let { onSelected(it) }
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
    onSelected: (AfterTappingByCartCheckbox?) -> Unit,
    selected: AfterTappingByCartCheckbox
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_after_tapping_cart_checkbox))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_tapping_cart_checkbox_change_status),
            selected = selected == AfterTappingByCartCheckbox.ChangeCartStatus,
            onClick = { onSelected(AfterTappingByCartCheckbox.ChangeCartStatus) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_after_tapping_cart_checkbox_do_nothing),
            selected = selected == AfterTappingByCartCheckbox.DoNothing,
            onClick = { onSelected(AfterTappingByCartCheckbox.DoNothing) }
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
        title = { Text(stringResource(R.string.settings_text_cart_checkbox_color)) },
        body = {
            Text(data.text.asCompose())
            CheckboxColorMenu(
                expanded = expanded,
                onSelected = { newColor ->
                    expanded = false
                    newColor?.let { onSelected(it) }
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
        DropdownMenuTitle(stringResource(R.string.settings_text_cart_checkbox_color))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_cart_checkbox_color_red_green),
            selected = selected == CheckboxColor.RedOrGreen,
            onClick = { onSelected(CheckboxColor.RedOrGreen) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_cart_checkbox_color_red_grey),
            selected = selected == CheckboxColor.RedOrGray,
            onClick = { onSelected(CheckboxColor.RedOrGray) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_cart_checkbox_color_grey),
            selected = selected == CheckboxColor.Gray,
            onClick = { onSelected(CheckboxColor.Gray) }
        )
    }
}

// SWIPE CART

@Composable
private fun SwipeCartLeftItem(
    data: SettingsItem<SwipeCart.Left>,
    onSelected: (SwipeCart.Left) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_swipe_cart_left)) },
        body = {
            Text(data.text.asCompose())
            SwipeCartMenu(
                expanded = expanded,
                title = stringResource(R.string.settings_text_swipe_cart_left),
                onSelected = { newActionName ->
                    expanded = false
                    newActionName?.let { onSelected(SwipeCart.Left(it)) }
                },
                selected = data.selected.actionName
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun SwipeCartRightItem(
    data: SettingsItem<SwipeCart.Right>,
    onSelected: (SwipeCart.Right) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_swipe_cart_right)) },
        body = {
            Text(data.text.asCompose())
            SwipeCartMenu(
                expanded = expanded,
                title = stringResource(R.string.settings_text_swipe_cart_right),
                onSelected = { newActionName ->
                    expanded = false
                    newActionName?.let { onSelected(SwipeCart.Right(it)) }
                },
                selected = data.selected.actionName
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun SwipeCartMenu(
    expanded: Boolean,
    title: String,
    onSelected: (SwipeCartActionName?) -> Unit,
    selected: SwipeCartActionName
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(title)

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_swipe_cart_archive_unarchive_cart),
            selected = selected == SwipeCartActionName.ArchiveOrUnarchiveCart,
            onClick = { onSelected(SwipeCartActionName.ArchiveOrUnarchiveCart) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_swipe_cart_delete_restore_cart),
            selected = selected == SwipeCartActionName.DeleteOrRestoreCart,
            onClick = { onSelected(SwipeCartActionName.DeleteOrRestoreCart) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_swipe_cart_delete_products),
            selected = selected == SwipeCartActionName.DeleteProducts,
            onClick = { onSelected(SwipeCartActionName.DeleteProducts) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_swipe_cart_change_cart_status),
            selected = selected == SwipeCartActionName.ChangeCartStatusToCompletedOrActive,
            onClick = { onSelected(SwipeCartActionName.ChangeCartStatusToCompletedOrActive) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_swipe_cart_off),
            selected = selected == SwipeCartActionName.Off,
            onClick = { onSelected(SwipeCartActionName.Off) }
        )
    }
}

// DELETION FROM TRASH

@Composable
private fun DeletionFromTrashItem(
    data: SettingsItem<DeletionCartFromTrash>,
    onSelected: (DeletionCartFromTrash) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_deletion_cart_from_trash)) },
        body = {
            Text(data.text.asCompose())
            DeletionFromTrashMenu(
                expanded = expanded,
                onSelected = { newDeletionFromTrash ->
                    expanded = false
                    newDeletionFromTrash?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun DeletionFromTrashMenu(
    expanded: Boolean,
    onSelected: (DeletionCartFromTrash?) -> Unit,
    selected: DeletionCartFromTrash
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_deletion_cart_from_trash))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_deletion_cart_from_trash_after_7_days),
            selected = selected == DeletionCartFromTrash.After7Days,
            onClick = { onSelected(DeletionCartFromTrash.After7Days) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_deletion_cart_from_trash_do_not_delete),
            selected = selected == DeletionCartFromTrash.DoNotDelete,
            onClick = { onSelected(DeletionCartFromTrash.DoNotDelete) }
        )
    }
}