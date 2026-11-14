package ru.sokolovromann.myshopping.feature.carts.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.sokolovromann.myshopping.core.domain.model.PurchasesStatus
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.component.BottomGridSpacer
import ru.sokolovromann.myshopping.core.ui.component.GridBar
import ru.sokolovromann.myshopping.core.ui.component.GridSelectableItem
import ru.sokolovromann.myshopping.core.ui.component.LoadingContent
import ru.sokolovromann.myshopping.core.ui.component.NotFoundContent
import ru.sokolovromann.myshopping.core.ui.component.SimpleVerticalGrid
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.carts.R
import ru.sokolovromann.myshopping.feature.carts.model.CartItem
import ru.sokolovromann.myshopping.feature.carts.model.PurchasesState

@Composable
fun PurchasesContent(
    onItemClick: (UID) -> Unit,
    onItemLongClick: (UID) -> Unit,
    purchasesState: PurchasesState
) {
    if (purchasesState.isLoading) {
        LoadingContent()
    } else {
        when (purchasesState.status) {
            PurchasesStatus.Found -> FoundContent(
                onItemClick,
                onItemLongClick,
                purchasesState
            )
            PurchasesStatus.NotFound -> NotFoundContent(
                text = UiText.FromResources(R.string.purchases_text_not_found_purchases)
            )
        }
    }
}

@Composable
private fun FoundContent(
    onItemClick: (UID) -> Unit,
    onItemLongClick: (UID) -> Unit,
    purchasesState: PurchasesState
) {
    SimpleVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        cells = purchasesState.itemsCells
    ) {
        if (purchasesState.pinnedItems.isNotEmpty()) {
            item(span = StaggeredGridItemSpan.SingleLane) {
                Header(stringResource(R.string.purchases_text_pinned))
            }
        }
        items(purchasesState.pinnedItems, key = { it.uid.value }) { item ->
            val isSelected = purchasesState.itemsSelected.contains(item.uid)
            GridSelectableItem(
                isSelected = isSelected,
                title = { item.name?.let { Text(it) } },
                body = { BodyText(item) },
                onClick = { onItemClick(item.uid) },
                onLongClick = { onItemLongClick(item.uid) }
            )
        }
        if (purchasesState.pinnedItems.isNotEmpty()) {
            item(span = StaggeredGridItemSpan.SingleLane) {
                Header(stringResource(R.string.purchases_text_other))
            }
        }
        items(purchasesState.otherItems, key = { it.uid.value }) { item ->
            val isSelected = purchasesState.itemsSelected.contains(item.uid)
            GridSelectableItem(
                isSelected = isSelected,
                title = { item.name?.let { Text(it) } },
                body = { BodyText(item) },
                onClick = { onItemClick(item.uid) },
                onLongClick = { onItemLongClick(item.uid) }
            )
        }
        item { BottomGridSpacer() }
    }
}

@Composable
private fun Header(text: String) {
    Text(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
        text = text,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.labelLarge
    )
}

@Composable
private fun BodyText(item: CartItem) {
    Column {
        Text(item.pinnedProducts.toString())
        Text(item.otherProducts.toString())
        item.reminder?.let { Text(it) }
        item.total?.let { Text(it.asCompose()) }
    }
}