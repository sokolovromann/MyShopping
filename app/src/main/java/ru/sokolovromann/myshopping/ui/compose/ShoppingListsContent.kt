package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.*
import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListItem
import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListLocation
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.utils.*

@Composable
fun ShoppingListsGrid(
    modifier: Modifier = Modifier,
    multiColumns: Boolean,
    items: List<ShoppingListItem>,
    fontSize: FontSize,
    dropdownMenu: @Composable ((String) -> Unit)? = null,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit
) {
    AppGrid(
        modifier = modifier,
        multiColumns = multiColumns
    ) {
        items.forEach { item ->
            AppSurfaceItem(
                modifier = modifier,
                title = getShoppingListItemTitleOrNull(item.nameText, fontSize),
                body = {
                    ShoppingListItemBody(
                        products = item.productsList,
                        total = item.totalText,
                        reminder = item.reminderText,
                        fontSize = fontSize
                    )
                },
                dropdownMenu = { dropdownMenu?.let { it(item.uid) } },
                onClick = { onClick(item.uid) },
                onLongClick = { onLongClick(item.uid) }
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
                after = { CheckmarkAppCheckbox(checked = displayTotal == DisplayTotal.ALL) }
            )
            AppDropdownMenuItem(
                onClick = { onSelected(DisplayTotal.COMPLETED) },
                text = { Text(text = stringResource(R.string.shoppingLists_action_displayCompletedTotal)) },
                after = { CheckmarkAppCheckbox(checked = displayTotal == DisplayTotal.COMPLETED) }
            )
            AppDropdownMenuItem(
                onClick = { onSelected(DisplayTotal.ACTIVE) },
                text = { Text(text = stringResource(R.string.shoppingLists_action_displayActiveTotal)) },
                after = { CheckmarkAppCheckbox(checked = displayTotal == DisplayTotal.ACTIVE) }
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
        modifier = modifier,
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
                after = { CheckmarkAppCheckbox(checked = location == ShoppingListLocation.PURCHASES) }
            )
            AppDropdownMenuItem(
                onClick = { onSelected(ShoppingListLocation.ARCHIVE) },
                text = { Text(text = stringResource(R.string.shoppingLists_action_selectArchiveLocation)) },
                after = { CheckmarkAppCheckbox(checked = location == ShoppingListLocation.ARCHIVE) }
            )
            AppDropdownMenuItem(
                onClick = { onSelected(ShoppingListLocation.TRASH) },
                text = { Text(text = stringResource(R.string.shoppingLists_action_selectTrashLocation)) },
                after = { CheckmarkAppCheckbox(checked = location == ShoppingListLocation.TRASH) }
            )
        }
    }
}

@Composable
private fun getShoppingListItemTitleOrNull(
    name: UiText,
    fontSize: FontSize
) = itemOrNull(enabled = name.asCompose().isNotEmpty()) {
    Text(
        modifier = Modifier.padding(ShoppingListItemTextMediumPaddings),
        text = name.asCompose(),
        fontSize = fontSize.toItemTitle().sp
    )
}

@Composable
private fun ShoppingListItemBody(
    products: List<Pair<Boolean?, UiText>>,
    total: UiText,
    reminder: UiText,
    fontSize: FontSize,
) {
    val itemFontSize = fontSize.toItemBody()

    Column {
        products.forEach {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(ShoppingListItemTextSmallPaddings)
            ) {
                it.first?.let { completed ->
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
                    Spacer(modifier = Modifier.size(ShoppingListItemSpacerSize))
                }

                Text(
                    text = it.second.asCompose(),
                    fontSize = itemFontSize.sp
                )
            }
        }

        val totalAsCompose = total.asCompose()
        val reminderAsCompose = reminder.asCompose()
        if (totalAsCompose.isNotEmpty() || reminderAsCompose.isNotEmpty()) {
            Spacer(modifier = Modifier.size(ShoppingListItemSpacerSize))
        }

        if (totalAsCompose.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(ShoppingListItemTextMediumPaddings),
                text = totalAsCompose,
                fontSize = itemFontSize.sp
            )
        }

        if (reminderAsCompose.isNotEmpty()) {
            Row(
                modifier = Modifier.padding(ShoppingListItemTextMediumPaddings),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(itemFontSize.dp),
                    painter = painterResource(R.drawable.ic_all_reminder),
                    contentDescription = "",
                    tint = MaterialTheme.colors.primary.copy(ContentAlpha.medium)
                )
                Spacer(modifier = Modifier.size(ShoppingListItemSpacerSize))
                Text(
                    text = reminderAsCompose,
                    fontSize = itemFontSize.sp
                )
            }
        }
    }
}

private val ShoppingListItemTextSmallPaddings = PaddingValues(vertical = 2.dp)
private val ShoppingListItemTextMediumPaddings = PaddingValues(vertical = 4.dp)
private val ShoppingListItemSpacerSize = 4.dp