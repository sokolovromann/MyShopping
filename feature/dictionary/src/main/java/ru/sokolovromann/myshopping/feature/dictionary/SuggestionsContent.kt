package ru.sokolovromann.myshopping.feature.dictionary

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
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
                    onClick = { onItemClick(item.uid) },
                    onLongClick = { onItemLongClick(item.uid) }
                ) {
                    Text(
                        text = item.name.asCompose(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    item.quantities?.let { SuggestionsItemBodyText(it) }
                    item.unitPrices?.let { SuggestionsItemBodyText(it) }
                    item.discounts?.let { SuggestionsItemBodyText(it) }
                    item.taxes?.let { SuggestionsItemBodyText(it) }
                    item.costs?.let { SuggestionsItemBodyText(it) }
                    item.manufacturers?.let { SuggestionsItemBodyText(it) }
                    item.brands?.let { SuggestionsItemBodyText(it) }
                    item.sizes?.let { SuggestionsItemBodyText(it) }
                    item.colors?.let { SuggestionsItemBodyText(it) }
                }
            }
            item { BottomGridSpacer() }
        }
    }
}

@Composable
private fun SuggestionsItemBodyText(text: UiText) {
    Text(
        text = text.asCompose(),
        style = MaterialTheme.typography.bodyMedium
    )
}