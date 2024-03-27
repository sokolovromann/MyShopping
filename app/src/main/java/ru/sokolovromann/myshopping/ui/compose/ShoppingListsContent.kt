package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.ui.model.SelectedValue
import ru.sokolovromann.myshopping.ui.model.ShoppingListItem
import ru.sokolovromann.myshopping.ui.model.UiFontSize
import ru.sokolovromann.myshopping.ui.model.UiIcon
import ru.sokolovromann.myshopping.ui.model.UiString

@Composable
fun ShoppingListsGrid(
    modifier: Modifier = Modifier,
    multiColumns: Boolean,
    deviceSize: DeviceSize,
    pinnedItems: List<ShoppingListItem> = listOf(),
    otherItems: List<ShoppingListItem>,
    displayProducts: DisplayProducts,
    displayCompleted: DisplayCompleted,
    coloredCheckbox: Boolean,
    topBar: @Composable (RowScope.() -> Unit)? = null,
    bottomBar: @Composable (RowScope.() -> Unit)? = null,
    isWaiting: Boolean,
    notFound: @Composable (ColumnScope.() -> Unit)? = null,
    isNotFound: Boolean,
    fontSize: UiFontSize,
    dropdownMenu: @Composable ((String) -> Unit)? = null,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    selectedUids: List<String>? = null
) {
    SmartphoneTabletAppGrid(
        modifier = modifier,
        multiColumns = multiColumns,
        multiColumnsSpace = true,
        deviceSize = deviceSize,
        topBar = topBar,
        bottomBar = bottomBar,
        isWaiting = isWaiting,
        notFound = notFound,
        isNotFound = isNotFound
    ) {
        if (pinnedItems.isNotEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine) {
                AppTextGridHeader(
                    text = stringResource(R.string.shoppingLists_text_pinnedShoppingLists),
                    fontSize = fontSize
                )
            }

            items(pinnedItems) { item ->
                val selected = selectedUids?.contains(item.uid) ?: false

                AppSurfaceItem(
                    title = getShoppingListItemTitleOrNull(item.name, fontSize),
                    body = {
                        ShoppingListItemBody(
                            hasName = item.name.asCompose().isNotEmpty(),
                            products = item.products,
                            displayProducts = displayProducts,
                            total = item.total,
                            reminder = item.reminder,
                            fontSize = fontSize,
                            coloredCheckbox = coloredCheckbox
                        )
                    },
                    left = getShoppingListItemLeftOrNull(
                        highlightCheckbox = coloredCheckbox,
                        checked = item.completed,
                        displayProducts = displayProducts
                    ),
                    right = getShoppingListItemRightOrNull(selected),
                    dropdownMenu = { dropdownMenu?.let { it(item.uid) } },
                    onClick = { onClick(item.uid) },
                    onLongClick = { onLongClick(item.uid) },
                    backgroundColor = getAppItemBackgroundColor(selected, item.completed, displayCompleted == DisplayCompleted.NO_SPLIT)
                )
            }

            if (otherItems.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    AppTextGridHeader(
                        text = stringResource(R.string.shoppingLists_text_otherShoppingLists),
                        fontSize = fontSize
                    )
                }
            }
        }

        items(otherItems) { item ->
            val selected = selectedUids?.contains(item.uid) ?: false

            AppSurfaceItem(
                title = getShoppingListItemTitleOrNull(item.name, fontSize),
                body = {
                    ShoppingListItemBody(
                        hasName = item.name.asCompose().isNotEmpty(),
                        products = item.products,
                        displayProducts = displayProducts,
                        total = item.total,
                        reminder = item.reminder,
                        fontSize = fontSize,
                        coloredCheckbox = coloredCheckbox
                    )
                },
                left = getShoppingListItemLeftOrNull(
                    highlightCheckbox = coloredCheckbox,
                    checked = item.completed,
                    displayProducts = displayProducts
                ),
                right = getShoppingListItemRightOrNull(selected),
                dropdownMenu = { dropdownMenu?.let { it(item.uid) } },
                onClick = { onClick(item.uid) },
                onLongClick = { onLongClick(item.uid) },
                backgroundColor = getAppItemBackgroundColor(selected, item.completed, displayCompleted == DisplayCompleted.NO_SPLIT)
            )
        }
    }
}

@Composable
fun ShoppingListsTotalContent(
    modifier: Modifier = Modifier,
    displayTotal: DisplayTotal,
    totalText: UiString,
    fontSize: TextUnit,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onSelected: (DisplayTotal) -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = { onExpanded(true) }
    ) {
        Text(
            text = totalText.asCompose(),
            fontSize = fontSize
        )
        AppDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpanded(false) },
            header = { Text(text = stringResource(R.string.shoppingLists_header_displayTotal)) }
        ) {
            AppDropdownMenuItem(
                onClick = { onSelected(DisplayTotal.ALL) },
                text = { Text(text = stringResource(R.string.shoppingLists_action_displayAllTotal)) },
                right = { CheckmarkAppCheckbox(checked = displayTotal == DisplayTotal.ALL) }
            )
            AppDropdownMenuItem(
                onClick = { onSelected(DisplayTotal.COMPLETED) },
                text = { Text(text = stringResource(R.string.shoppingLists_action_displayCompletedTotal)) },
                right = { CheckmarkAppCheckbox(checked = displayTotal == DisplayTotal.COMPLETED) }
            )
            AppDropdownMenuItem(
                onClick = { onSelected(DisplayTotal.ACTIVE) },
                text = { Text(text = stringResource(R.string.shoppingLists_action_displayActiveTotal)) },
                right = { CheckmarkAppCheckbox(checked = displayTotal == DisplayTotal.ACTIVE) }
            )
        }
    }
}

@Composable
fun ShoppingListsLocationContent(
    modifier: Modifier = Modifier,
    location: SelectedValue<ShoppingLocation>,
    fontSize: TextUnit,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onSelected: (ShoppingLocation) -> Unit
) {
    TextButton(
        modifier = Modifier
            .padding(ShoppingListsLocationPaddings)
            .then(modifier),
        onClick = { onExpanded(true) }
    ) {
        Text(
            text = location.text.asCompose(),
            fontSize = fontSize
        )
        AppDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpanded(false) },
            header = { Text(text = stringResource(R.string.shoppingLists_header_location)) }
        ) {
            AppDropdownMenuItem(
                onClick = { onSelected(ShoppingLocation.PURCHASES) },
                text = { Text(text = stringResource(R.string.shoppingLists_action_selectPurchasesLocation)) },
                right = { CheckmarkAppCheckbox(checked = location.selected == ShoppingLocation.PURCHASES) }
            )
            AppDropdownMenuItem(
                onClick = { onSelected(ShoppingLocation.ARCHIVE) },
                text = { Text(text = stringResource(R.string.shoppingLists_action_selectArchiveLocation)) },
                right = { CheckmarkAppCheckbox(checked = location.selected == ShoppingLocation.ARCHIVE) }
            )
        }
    }
}

@Composable
fun ShoppingListsHiddenContent(
    fontSize: UiFontSize,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(ShoppingListsHiddenProductsPaddings),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.shoppingLists_text_hiddenShoppingLists),
            fontSize = fontSize.itemBody.sp,
            color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.shoppingLists_contentDescription_displayCompletedPurchasesIcon),
                tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
            )
        }
    }
}

@Composable
fun ShoppingListsDisplayProductsMenu(
    expanded: Boolean,
    displayProducts: DisplayProducts,
    onDismissRequest: () -> Unit,
    onSelected: (DisplayProducts) -> Unit
) {
    AppDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        header = { Text(text = stringResource(id = R.string.shoppingLists_action_displayProducts)) }
    ) {
        AppDropdownMenuItem(
            onClick = { onSelected(DisplayProducts.VERTICAL) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayProductsVertically)) },
            right = { CheckmarkAppCheckbox(checked = displayProducts == DisplayProducts.VERTICAL) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(DisplayProducts.HORIZONTAL) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayProductsHorizontally)) },
            right = { CheckmarkAppCheckbox(checked = displayProducts == DisplayProducts.HORIZONTAL) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(DisplayProducts.HIDE) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_hideProducts)) },
            right = { CheckmarkAppCheckbox(checked = displayProducts == DisplayProducts.HIDE) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(DisplayProducts.HIDE_IF_HAS_TITLE) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_hideProductsIfHasTitle)) },
            right = { CheckmarkAppCheckbox(checked = displayProducts == DisplayProducts.HIDE_IF_HAS_TITLE) }
        )
    }
}

@Composable
fun ShoppingListsSortByMenu(
    expanded: Boolean,
    sortValue: SelectedValue<Sort>,
    sortFormatted: Boolean,
    onDismissRequest: () -> Unit,
    onSelected: (SortBy) -> Unit,
    onReverse: () -> Unit,
    onInvertSortFormatted: () -> Unit
) {
    AppDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        header = { Text(text = stringResource(id = R.string.shoppingLists_action_sort)) }
    ) {
        AppDropdownMenuItem(
            onClick = { onSelected(SortBy.CREATED) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByCreated)) },
            right = {
                val checked = sortFormatted && sortValue.selected.sortBy == SortBy.CREATED
                CheckmarkAppCheckbox(checked = checked)
            }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(SortBy.LAST_MODIFIED) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByLastModified)) },
            right = {
                val checked = sortFormatted && sortValue.selected.sortBy == SortBy.LAST_MODIFIED
                CheckmarkAppCheckbox(checked = checked)
            }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(SortBy.NAME) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByName)) },
            right = {
                val checked = sortFormatted && sortValue.selected.sortBy == SortBy.NAME
                CheckmarkAppCheckbox(checked = checked)
            }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(SortBy.TOTAL) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByTotal)) },
            right = {
                val checked = sortFormatted && sortValue.selected.sortBy == SortBy.TOTAL
                CheckmarkAppCheckbox(checked = checked)
            }
        )
        Divider()
        AppDropdownMenuItem(
            onClick = onReverse,
            text = { Text(text = stringResource(R.string.shoppingLists_action_reverseSort)) },
            right = {
                if (sortFormatted) {
                    val checked = !sortValue.selected.ascending
                    AppSwitch(checked = checked)
                }
            }
        )
        Divider()
        AppDropdownMenuItem(
            onClick = onInvertSortFormatted ,
            text = { Text(text = stringResource(R.string.shoppingLists_action_automaticSorting)) },
            right = { AppSwitch(checked = sortFormatted) }
        )
    }
}

@Composable
fun ShoppingListsOpenNavigationButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        NavigationMenuIcon(
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_navigationMenuIcon)
        )
    }
}

@Composable
fun ShoppingListsCancelSearchButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        CancelSearchIcon(
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_cancelSearchIcon)
        )
    }
}

@Composable
fun ShoppingListsCancelSelectionButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        CancelSelectionIcon(
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_cancelSelectionIcon),
        )
    }
}

@Composable
fun ShoppingListsDeleteDataButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        DeleteDataIcon(
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_deleteDataIcon),
        )
    }
}

@Composable
fun ShoppingListsArchiveDataButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        AppTopBarIcon(
            icon = UiIcon.FromResources(R.drawable.ic_all_archive),
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_archiveIcon)
        )
    }
}

@Composable
fun ShoppingListsUnarchiveDataButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        AppTopBarIcon(
            icon = UiIcon.FromResources(R.drawable.ic_all_unarchive),
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_unarchiveIcon)
        )
    }
}

@Composable
fun ShoppingListsRestoreDataButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        AppTopBarIcon(
            icon = UiIcon.FromResources(R.drawable.ic_all_restore),
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_restoreIcon)
        )
    }
}

@Composable
fun ShoppingListsSelectAllDataButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        SelectAllDataIcon(
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_selectAllDataIcon)
        )
    }
}

@Composable
private fun getShoppingListItemLeftOrNull(
    highlightCheckbox: Boolean,
    checked: Boolean,
    displayProducts: DisplayProducts
) = itemOrNull(enabled = displayProducts == DisplayProducts.HIDE || displayProducts == DisplayProducts.HIDE_IF_HAS_TITLE) {
    val checkedColor = if (highlightCheckbox) {
        MaterialTheme.colors.primary.copy(alpha = ContentAlpha.medium)
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }

    val uncheckedColor = if (highlightCheckbox) {
        MaterialTheme.colors.error.copy(alpha = ContentAlpha.medium)
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    }

    AppCheckbox(
        checked = checked,
        colors = CheckboxDefaults.colors(
            checkedColor = checkedColor,
            uncheckedColor = uncheckedColor
        )
    )
}

@Composable
private fun getShoppingListItemTitleOrNull(
    name: UiString,
    fontSize: UiFontSize
) = itemOrNull(enabled = name.asCompose().isNotEmpty()) {
    Column {
        Spacer(modifier = Modifier.size(ShoppingListItemSpacerMediumSize))
        Text(
            text = name.asCompose(),
            fontSize = fontSize.itemTitle.sp
        )
    }
}

@Composable
private fun ShoppingListItemBody(
    hasName: Boolean,
    products: List<Pair<Boolean?, UiString>>,
    displayProducts: DisplayProducts,
    total: UiString,
    reminder: UiString,
    fontSize: UiFontSize,
    coloredCheckbox: Boolean
) {
    val itemFontSize = fontSize.itemBody

    Column {
        val reminderAsCompose = reminder.asCompose()
        val hasReminder = reminderAsCompose.isNotEmpty()
        if (hasReminder) {
            Spacer(modifier = Modifier.size(ShoppingListItemSpacerSmallSize))
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(itemFontSize.dp),
                    painter = painterResource(R.drawable.ic_all_reminder),
                    contentDescription = "",
                    tint = MaterialTheme.colors.primary.copy(ContentAlpha.medium)
                )
                Spacer(modifier = Modifier.size(ShoppingListItemSpacerMediumSize))
                Text(
                    text = reminderAsCompose,
                    color = MaterialTheme.colors.primary,
                    fontSize = itemFontSize.sp
                )
            }
        }

        when (displayProducts) {
            DisplayProducts.VERTICAL -> {
                if (hasName || hasReminder) {
                    Spacer(modifier = Modifier.size(ShoppingListItemSpacerLargeSize))
                }

                ShoppingsListsItemProducts(
                    products = products,
                    spacerAfterIcon = true,
                    fontSize = fontSize,
                    showCheckbox = true,
                    coloredCheckbox = coloredCheckbox
                )
            }

            DisplayProducts.HORIZONTAL -> {
                if (hasName) {
                    if (hasReminder) {
                        Spacer(modifier = Modifier.size(ShoppingListItemSpacerLargeSize))
                    } else {
                        Spacer(modifier = Modifier.size(ShoppingListItemSpacerSmallSize))
                    }
                }

                Row {
                    ShoppingsListsItemProducts(
                        products = products,
                        spacerAfterIcon = false,
                        fontSize = fontSize,
                        showCheckbox = true,
                        coloredCheckbox = coloredCheckbox)
                }
            }

            DisplayProducts.HIDE -> {}

            DisplayProducts.HIDE_IF_HAS_TITLE -> {
                if (!hasName) {
                    Row {
                        ShoppingsListsItemProducts(
                            products = products,
                            spacerAfterIcon = false,
                            fontSize = fontSize,
                            showCheckbox = false,
                            coloredCheckbox = coloredCheckbox
                        )
                    }
                }
            }
        }

        val totalAsCompose = total.asCompose()
        if (totalAsCompose.isNotEmpty()) {
            if (displayProducts == DisplayProducts.VERTICAL) {
                Spacer(modifier = Modifier.size(ShoppingListItemSpacerMediumSize))
            }

            Text(
                modifier = Modifier.padding(ShoppingListItemTextMediumPaddings),
                text = totalAsCompose,
                fontSize = itemFontSize.sp
            )
        }
    }
}

@Composable
private fun ShoppingsListsItemProducts(
    products: List<Pair<Boolean?, UiString>>,
    spacerAfterIcon: Boolean,
    fontSize: UiFontSize,
    showCheckbox: Boolean = true,
    coloredCheckbox: Boolean
) {
    val itemFontSize = fontSize.itemBody

    products.forEach {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(ShoppingListItemTextSmallPaddings)
        ) {
            it.first?.let { completed ->
                if (!showCheckbox) {
                    return@let
                }

                val painter: Painter = if (completed) {
                    painterResource(R.drawable.ic_all_check_box)
                } else {
                    painterResource(R.drawable.ic_all_check_box_outline)
                }

                val tint = (if (coloredCheckbox) {
                    if (completed) {
                        MaterialTheme.colors.primary
                    } else {
                        MaterialTheme.colors.error
                    }
                } else {
                    contentColorFor(MaterialTheme.colors.onSurface)
                }).copy(ContentAlpha.medium)

                Icon(
                    modifier = Modifier.size(itemFontSize.dp),
                    painter = painter,
                    contentDescription = "",
                    tint = tint
                )

                if (spacerAfterIcon) {
                    Spacer(modifier = Modifier.size(ShoppingListItemSpacerMediumSize))
                }
            }

            Text(
                text = it.second.asCompose(),
                fontSize = itemFontSize.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!spacerAfterIcon) {
                Spacer(modifier = Modifier.size(ShoppingListItemSpacerMediumSize))
            }
        }
    }
}

@Composable
private fun getShoppingListItemRightOrNull(
    selected: Boolean
) = itemOrNull(enabled = selected) {
    CheckmarkAppCheckbox(
        checked = true,
        checkmarkColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    )
}

private val ShoppingListItemTextSmallPaddings = PaddingValues(vertical = 2.dp)
private val ShoppingListItemTextMediumPaddings = PaddingValues(vertical = 4.dp)
private val ShoppingListItemSpacerSmallSize = 2.dp
private val ShoppingListItemSpacerMediumSize = 4.dp
private val ShoppingListItemSpacerLargeSize = 8.dp
private val ShoppingListsHiddenProductsPaddings = PaddingValues(
    start = 8.dp,
    top = 8.dp,
    end = 8.dp
)
private val ShoppingListsLocationPaddings = PaddingValues(
    horizontal = 8.dp
)
val ShoppingListsSearchPaddings = PaddingValues(
    horizontal = 8.dp
)