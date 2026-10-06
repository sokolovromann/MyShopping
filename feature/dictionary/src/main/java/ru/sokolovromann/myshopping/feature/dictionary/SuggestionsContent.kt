package ru.sokolovromann.myshopping.feature.dictionary

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.component.BottomGridSpacer
import ru.sokolovromann.myshopping.core.ui.component.GridSelectableItem
import ru.sokolovromann.myshopping.core.ui.component.NotFoundContent
import ru.sokolovromann.myshopping.core.ui.component.SimpleVerticalGrid
import ru.sokolovromann.myshopping.core.ui.model.UiText

@Composable
fun SuggestionsContent(
    onItemClick: (UID) -> Unit,
    onItemLongClick: (UID) -> Unit,
    state: SuggestionsState
) {
    if (state.isNotFound) {
        NotFoundContent(
            text = UiText.FromResources(R.string.dictionary_text_suggestions_not_found)
        )
    } else {
        SimpleVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            cells = state.cells
        ) {
            items(state.items, key = { it.uid.value }) { item ->
                val isSelected = state.selected.contains(item.uid)
                GridSelectableItem(
                    isSelected = isSelected,
                    title = { Text(item.name.asCompose(),) },
                    body = {
                        item.quantities?.let { Text(it.asCompose()) }
                        item.unitPrices?.let { Text(it.asCompose()) }
                        item.discounts?.let { Text(it.asCompose()) }
                        item.taxes?.let { Text(it.asCompose()) }
                        item.costs?.let { Text(it.asCompose()) }
                        item.manufacturers?.let { Text(it.asCompose()) }
                        item.brands?.let { Text(it.asCompose()) }
                        item.sizes?.let { Text(it.asCompose()) }
                        item.colors?.let { Text(it.asCompose()) }
                    },
                    onClick = { onItemClick(item.uid) },
                    onLongClick = { onItemLongClick(item.uid) }
                )
            }
            item { BottomGridSpacer() }
        }
    }
}