package ru.sokolovromann.myshopping.feature.dictionary.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import ru.sokolovromann.myshopping.core.domain.model.UID
import ru.sokolovromann.myshopping.core.ui.component.BottomGridSpacer
import ru.sokolovromann.myshopping.core.ui.component.GridSelectableItem
import ru.sokolovromann.myshopping.core.ui.component.LoadingContent
import ru.sokolovromann.myshopping.core.ui.component.NotFoundContent
import ru.sokolovromann.myshopping.core.ui.component.SimpleVerticalGrid
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.dictionary.R
import ru.sokolovromann.myshopping.feature.dictionary.model.SuggestionsState

@Composable
fun SuggestionsContent(
    onItemClick: (UID) -> Unit,
    onItemLongClick: (UID) -> Unit,
    suggestionsState: SuggestionsState
) {
    if (suggestionsState.isLoading) {
        LoadingContent()
    } else {
        if (suggestionsState.items.isEmpty()) {
            NotFoundContent(
                text = UiText.FromResources(R.string.dictionary_text_suggestions_not_found)
            )
        } else {
            SimpleVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                cells = suggestionsState.itemsCells
            ) {
                items(suggestionsState.items, key = { it.uid.value }) { item ->
                    val isSelected = suggestionsState.itemsSelected.contains(item.uid)
                    GridSelectableItem(
                        isSelected = isSelected,
                        title = { Text(item.name.asCompose()) },
                        body = {
                            if (item.fabrics.isEmpty()) {
                                Text(stringResource(R.string.suggestions_items_no_details))
                            } else {
                                item.fabrics.forEach { BodyText(it.key, it.value) }
                            }
                        },
                        onClick = { onItemClick(item.uid) },
                        onLongClick = { onItemLongClick(item.uid) }
                    )
                }
                item { BottomGridSpacer() }
            }
        }
    }
}

@Composable
private fun BodyText(key: UiText, value: String) {
    Row {
        Text(
            text = key.asCompose(),
            maxLines = 1
        )
        Text(
            text = value,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}