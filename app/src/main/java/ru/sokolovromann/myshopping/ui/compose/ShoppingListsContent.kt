package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.model.DisplayProducts
import ru.sokolovromann.myshopping.data.model.DisplayTotal
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.Sort
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.data.model.SwipeShopping
import ru.sokolovromann.myshopping.ui.model.SelectedValue
import ru.sokolovromann.myshopping.ui.model.ShoppingListItem
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
    strikethroughCompletedProducts: Boolean,
    coloredCheckbox: Boolean,
    topBar: @Composable (RowScope.() -> Unit)? = null,
    bottomBar: @Composable (RowScope.() -> Unit)? = null,
    isWaiting: Boolean,
    notFound: @Composable (ColumnScope.() -> Unit)? = null,
    isNotFound: Boolean,
    dropdownMenu: @Composable ((String) -> Unit)? = null,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    swipeShoppingLeft: SwipeShopping = SwipeShopping.DISABLED,
    onSwipeLeft: (String) -> Unit = {},
    swipeShoppingRight: SwipeShopping = SwipeShopping.DISABLED,
    onSwipeRight: (String) -> Unit = {},
    selectedUids: List<String>? = null
) {
    val swipeLeftRightEnabled = swipeShoppingLeft != SwipeShopping.DISABLED ||
            swipeShoppingRight != SwipeShopping.DISABLED
    val swipeEnabled = selectedUids == null && swipeLeftRightEnabled

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
                AppHeaderItem(UiString.FromResources(R.string.shoppingLists_text_pinnedShoppingLists))
            }

            items(pinnedItems) { item ->
                val selected = selectedUids?.contains(item.uid) ?: false

                AppItemSwipeableWrapper(
                    enabled = swipeEnabled,
                    left = getSwipeShoppingContent(swipeShoppingLeft, item.completed),
                    onSwipeLeft = { onSwipeLeft(item.uid) },
                    right = getSwipeShoppingContent(swipeShoppingRight, item.completed),
                    backgroundColor = getSwipeBackgroundColor(item.completed),
                    onSwipeRight = { onSwipeRight(item.uid) }
                ) {
                    AppSurfaceItem(
                        title = getShoppingListItemTitleOrNull(item.name),
                        body = {
                            ShoppingListItemBody(
                                hasName = item.name.asCompose().isNotEmpty(),
                                products = item.products,
                                displayProducts = displayProducts,
                                strikethroughCompletedProducts = strikethroughCompletedProducts,
                                total = item.total,
                                reminder = item.reminder,
                                lastModified = item.lastModified,
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

            if (otherItems.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    AppHeaderItem(UiString.FromResources(R.string.shoppingLists_text_otherShoppingLists))
                }
            }
        }

        items(otherItems) { item ->
            val selected = selectedUids?.contains(item.uid) ?: false

            AppItemSwipeableWrapper(
                enabled = swipeEnabled,
                left = getSwipeShoppingContent(swipeShoppingLeft, item.completed),
                onSwipeLeft = { onSwipeLeft(item.uid) },
                right = getSwipeShoppingContent(swipeShoppingRight, item.completed),
                backgroundColor = getSwipeBackgroundColor(item.completed),
                onSwipeRight = { onSwipeRight(item.uid) }
            ) {
                AppSurfaceItem(
                    title = getShoppingListItemTitleOrNull(item.name),
                    body = {
                        ShoppingListItemBody(
                            hasName = item.name.asCompose().isNotEmpty(),
                            products = item.products,
                            displayProducts = displayProducts,
                            strikethroughCompletedProducts = strikethroughCompletedProducts,
                            total = item.total,
                            reminder = item.reminder,
                            lastModified = item.lastModified,
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
}

@Composable
fun ShoppingListsTotalContent(
    modifier: Modifier = Modifier,
    displayTotal: DisplayTotal,
    totalText: UiString,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onSelected: (DisplayTotal) -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = { onExpanded(true) }
    ) {
        Text(text = totalText.asCompose())
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
        Text(text = location.text.asCompose())
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
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(ShoppingListsHiddenProductsPaddings),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.shoppingLists_text_hiddenShoppingLists),
            color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick) {
            DefaultIcon(
                icon = UiIcon.DisplayHidden,
                contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_displayCompletedPurchasesIcon),
                tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
            )
        }
    }
}

@Composable
fun ShoppingListsViewMenu(
    expanded: Boolean,
    multiColumns: Boolean,
    onDismissRequest: () -> Unit,
    onSelected: (Boolean) -> Unit
) {
    AppDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        header = { Text(text = stringResource(id = R.string.shoppingLists_header_view)) }
    ) {
        AppDropdownMenuItem(
            onClick = { onSelected(false) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_selectListView)) },
            right = { CheckmarkAppCheckbox(checked = !multiColumns) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(true) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_selectGridView)) },
            right = { CheckmarkAppCheckbox(checked = multiColumns) }
        )
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
    IconButton(onClick) {
        DefaultIcon(
            icon = UiIcon.NavigationMenu,
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_navigationMenuIcon)
        )
    }
}

@Composable
fun ShoppingListsCancelSearchButton(onClick: () -> Unit) {
    IconButton(onClick) {
        DefaultIcon(
            icon = UiIcon.Cancel,
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_cancelSearchIcon)
        )
    }
}

@Composable
fun ShoppingListsCancelSelectionButton(onClick: () -> Unit) {
    IconButton(onClick) {
        DefaultIcon(
            icon = UiIcon.Cancel,
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_cancelSelectionIcon)
        )
    }
}

@Composable
fun ShoppingListsDeleteDataButton(onClick: () -> Unit) {
    IconButton(onClick) {
        DefaultIcon(
            icon = UiIcon.Delete,
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_deleteDataIcon)
        )
    }
}

@Composable
fun ShoppingListsArchiveDataButton(onClick: () -> Unit) {
    IconButton(onClick) {
        DefaultIcon(
            icon = UiIcon.Archive,
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_archiveIcon)
        )
    }
}

@Composable
fun ShoppingListsUnarchiveDataButton(onClick: () -> Unit) {
    IconButton(onClick) {
        DefaultIcon(
            icon = UiIcon.Unarchive,
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_unarchiveIcon)
        )
    }
}

@Composable
fun ShoppingListsRestoreDataButton(onClick: () -> Unit) {
    IconButton(onClick) {
        DefaultIcon(
            icon = UiIcon.Restore,
            contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_restoreIcon)
        )
    }
}

@Composable
fun ShoppingListsSelectAllDataButton(onClick: () -> Unit) {
    IconButton(onClick) {
        DefaultIcon(
            icon = UiIcon.SelectAll,
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
) = itemOrNull(enabled = name.asCompose().isNotEmpty()) {
    Column {
        Spacer(modifier = Modifier.size(ShoppingListItemSpacerMediumSize))
        Text(text = name.asCompose())
    }
}

@Composable
private fun ShoppingListItemBody(
    hasName: Boolean,
    products: List<Pair<Boolean?, UiString>>,
    displayProducts: DisplayProducts,
    strikethroughCompletedProducts: Boolean,
    total: UiString,
    reminder: UiString,
    lastModified: UiString,
    coloredCheckbox: Boolean
) {
    Column {
        val reminderAsCompose = reminder.asCompose()
        val hasReminder = reminderAsCompose.isNotEmpty()
        if (hasReminder) {
            Spacer(modifier = Modifier.size(ShoppingListItemSpacerSmallSize))
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconSize = MaterialTheme.typography.body2.fontSize.value.dp
                DefaultIcon(
                    modifier = Modifier.size(iconSize),
                    icon = UiIcon.Reminder,
                    tint = MaterialTheme.colors.primary.copy(ContentAlpha.medium)
                )
                Spacer(modifier = Modifier.size(ShoppingListItemSpacerMediumSize))
                Text(
                    text = reminderAsCompose,
                    color = MaterialTheme.colors.primary
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
                    strikethroughCompletedProducts = strikethroughCompletedProducts,
                    spacerAfterIcon = true,
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
                        strikethroughCompletedProducts = strikethroughCompletedProducts,
                        spacerAfterIcon = false,
                        showCheckbox = true,
                        coloredCheckbox = coloredCheckbox
                    )
                }
            }

            DisplayProducts.HIDE -> {}

            DisplayProducts.HIDE_IF_HAS_TITLE -> {
                if (!hasName) {
                    Row {
                        ShoppingsListsItemProducts(
                            products = products,
                            strikethroughCompletedProducts = strikethroughCompletedProducts,
                            spacerAfterIcon = false,
                            commaAfterProduct = true,
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
                text = totalAsCompose
            )
        }

        val lastModifiedAsCompose = lastModified.asCompose()
        if (lastModifiedAsCompose.isNotEmpty()) {
            if (displayProducts == DisplayProducts.VERTICAL) {
                Spacer(modifier = Modifier.size(ShoppingListItemSpacerMediumSize))
            }

            Text(
                modifier = Modifier.padding(ShoppingListItemTextMediumPaddings),
                text = lastModifiedAsCompose
            )
        }
    }
}

@Composable
private fun ShoppingsListsItemProducts(
    products: List<Pair<Boolean?, UiString>>,
    strikethroughCompletedProducts: Boolean,
    spacerAfterIcon: Boolean,
    commaAfterProduct: Boolean = false,
    showCheckbox: Boolean = true,
    coloredCheckbox: Boolean
) {
    products.forEachIndexed { index, it ->
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(ShoppingListItemTextSmallPaddings)
        ) {
            it.first?.let { completed ->
                if (!showCheckbox) {
                    return@let
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

                val iconSize = MaterialTheme.typography.body2.fontSize.value.dp
                DefaultIcon(
                    modifier = Modifier.size(iconSize),
                    icon = if (completed) UiIcon.Checkbox else UiIcon.CheckboxOutline,
                    tint = tint
                )

                if (spacerAfterIcon) {
                    Spacer(modifier = Modifier.size(ShoppingListItemSpacerMediumSize))
                }
            }

            val textDecoration = if (strikethroughCompletedProducts && it.first == true) {
                TextDecoration.LineThrough
            } else {
                TextDecoration.None
            }
            Text(
                text = it.second.asCompose(),
                textDecoration = textDecoration,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (commaAfterProduct && index < products.lastIndex) {
                Text(text = ",")
            }

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

@Composable
private fun getSwipeShoppingContent(
    swipeShopping: SwipeShopping,
    completed: Boolean
): @Composable (() -> Unit)? {
    return when (swipeShopping) {
        SwipeShopping.DISABLED -> null
        SwipeShopping.ARCHIVE -> {
            { DefaultIcon(UiIcon.Archive) }
        }
        SwipeShopping.DELETE -> {
            {
                DefaultIcon(
                    icon = UiIcon.Delete,
                    tint = MaterialTheme.colors.error
                )
            }
        }
        SwipeShopping.COMPLETE -> {
            if (completed) {
                { DefaultIcon(UiIcon.CheckboxOutline) }
            } else {
                { DefaultIcon(UiIcon.Checkbox) }
            }
        }
    }
}

@Composable
private fun getSwipeBackgroundColor(completed: Boolean): Color {
    return Color.Transparent
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