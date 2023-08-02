package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
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
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.compose.state.ScreenState
import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListItem
import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListLocation
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.utils.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShoppingListsGrid(
    modifier: Modifier = Modifier,
    screenState: ScreenState,
    multiColumns: Boolean,
    smartphoneScreen: Boolean,
    pinnedItems: List<ShoppingListItem> = listOf(),
    otherItems: List<ShoppingListItem>,
    displayProducts: DisplayProducts,
    displayCompleted: DisplayCompleted,
    coloredCheckbox: Boolean,
    topBar: @Composable (RowScope.() -> Unit)? = null,
    bottomBar: @Composable (RowScope.() -> Unit)? = null,
    notFound: @Composable (ColumnScope.() -> Unit)? = null,
    fontSize: FontSize,
    dropdownMenu: @Composable ((String) -> Unit)? = null,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    selectedUids: List<String>? = null
) {
    SmartphoneTabletAppGrid(
        modifier = modifier,
        screenState = screenState,
        multiColumns = multiColumns,
        multiColumnsSpace = true,
        smartphoneScreen = smartphoneScreen,
        topBar = topBar,
        bottomBar = bottomBar,
        notFound = notFound
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
                    title = getShoppingListItemTitleOrNull(item.nameText, fontSize),
                    body = {
                        ShoppingListItemBody(
                            hasName = item.nameText.asCompose().isNotEmpty(),
                            products = item.productsList,
                            displayProducts = displayProducts,
                            total = item.totalText,
                            reminder = item.reminderText,
                            fontSize = fontSize
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
                title = getShoppingListItemTitleOrNull(item.nameText, fontSize),
                body = {
                    ShoppingListItemBody(
                        hasName = item.nameText.asCompose().isNotEmpty(),
                        products = item.productsList,
                        displayProducts = displayProducts,
                        total = item.totalText,
                        reminder = item.reminderText,
                        fontSize = fontSize
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
    totalText: UiText,
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
    location: ShoppingListLocation,
    fontSize: TextUnit,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onSelected: (ShoppingListLocation) -> Unit
) {
    TextButton(
        modifier = Modifier
            .padding(ShoppingListsLocationPaddings)
            .then(modifier),
        onClick = { onExpanded(true) }
    ) {
        Text(
            text = location.getText().asCompose(),
            fontSize = fontSize
        )
        AppDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpanded(false) },
            header = { Text(text = stringResource(R.string.shoppingLists_header_location)) }
        ) {
            AppDropdownMenuItem(
                onClick = { onSelected(ShoppingListLocation.PURCHASES) },
                text = { Text(text = stringResource(R.string.shoppingLists_action_selectPurchasesLocation)) },
                right = { CheckmarkAppCheckbox(checked = location == ShoppingListLocation.PURCHASES) }
            )
            AppDropdownMenuItem(
                onClick = { onSelected(ShoppingListLocation.ARCHIVE) },
                text = { Text(text = stringResource(R.string.shoppingLists_action_selectArchiveLocation)) },
                right = { CheckmarkAppCheckbox(checked = location == ShoppingListLocation.ARCHIVE) }
            )
        }
    }
}

@Composable
fun ShoppingListsHiddenContent(
    fontSize: FontSize,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(ShoppingListsHiddenProductsPaddings),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.shoppingLists_text_hiddenShoppingLists),
            fontSize = fontSize.toItemBody().sp,
            color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.shoppingLists_contentDescription_displayCompletedPurchases),
                tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
            )
        }
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
    name: UiText,
    fontSize: FontSize
) = itemOrNull(enabled = name.asCompose().isNotEmpty()) {
    Column {
        Spacer(modifier = Modifier.size(ShoppingListItemSpacerMediumSize))
        Text(
            text = name.asCompose(),
            fontSize = fontSize.toItemTitle().sp
        )
    }
}

@Composable
private fun ShoppingListItemBody(
    hasName: Boolean,
    products: List<Pair<Boolean?, UiText>>,
    displayProducts: DisplayProducts,
    total: UiText,
    reminder: UiText,
    fontSize: FontSize,
) {
    val itemFontSize = fontSize.toItemBody()

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
            DisplayProducts.COLUMNS -> {
                if (hasName || hasReminder) {
                    Spacer(modifier = Modifier.size(ShoppingListItemSpacerLargeSize))
                }

                ShoppingsListsItemProducts(products, true, fontSize)
            }

            DisplayProducts.ROW -> {
                if (hasName) {
                    if (hasReminder) {
                        Spacer(modifier = Modifier.size(ShoppingListItemSpacerLargeSize))
                    } else {
                        Spacer(modifier = Modifier.size(ShoppingListItemSpacerSmallSize))
                    }
                }

                Row {
                    ShoppingsListsItemProducts(products, false, fontSize)
                }
            }

            DisplayProducts.HIDE -> {}

            DisplayProducts.HIDE_IF_HAS_TITLE -> {
                if (!hasName) {
                    Row {
                        ShoppingsListsItemProducts(products, false, fontSize, false)
                    }
                }
            }
        }

        val totalAsCompose = total.asCompose()
        if (totalAsCompose.isNotEmpty()) {
            if (displayProducts == DisplayProducts.COLUMNS) {
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
    products: List<Pair<Boolean?, UiText>>,
    spacerAfterIcon: Boolean,
    fontSize: FontSize,
    showCheckbox: Boolean = true
) {
    val itemFontSize = fontSize.toItemBody()

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

                Icon(
                    modifier = Modifier.size(itemFontSize.dp),
                    painter = painter,
                    contentDescription = "",
                    tint = contentColorFor(MaterialTheme.colors.onSurface)
                        .copy(ContentAlpha.medium)
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