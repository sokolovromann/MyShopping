package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListItem
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.utils.toItemBody
import ru.sokolovromann.myshopping.ui.utils.toItemTitle

@Composable
fun ShoppingListSurfaceItem(
    modifier: Modifier = Modifier,
    shoppingListItem: ShoppingListItem,
    fontSize: FontSize,
    dropdownMenu: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    AppSurfaceItem(
        modifier = modifier,
        title = getShoppingListItemTitleOrNull(shoppingListItem.nameText, fontSize),
        body = {
            ShoppingListItemBody(
                products = shoppingListItem.productsList,
                total = shoppingListItem.totalText,
                reminder = shoppingListItem.reminderText,
                fontSize = fontSize
            )
        },
        dropdownMenu = dropdownMenu,
        onClick = onClick,
        onLongClick = onLongClick
    )
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
        if (totalAsCompose.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(ShoppingListItemTextMediumPaddings),
                text = totalAsCompose,
                fontSize = itemFontSize.sp
            )
        }

        val reminderAsCompose = reminder.asCompose()
        if (reminderAsCompose.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(ShoppingListItemTextMediumPaddings),
                text = reminderAsCompose,
                fontSize = itemFontSize.sp
            )
        }
    }
}

private val ShoppingListItemTextSmallPaddings = PaddingValues(vertical = 2.dp)
private val ShoppingListItemTextMediumPaddings = PaddingValues(vertical = 4.dp)
private val ShoppingListItemSpacerSize = 4.dp