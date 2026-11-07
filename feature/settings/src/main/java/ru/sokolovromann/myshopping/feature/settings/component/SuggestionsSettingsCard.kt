package ru.sokolovromann.myshopping.feature.settings.component

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import ru.sokolovromann.myshopping.core.domain.model.SuggestionAddingMode
import ru.sokolovromann.myshopping.core.ui.component.DropdownMenuSelectableItem
import ru.sokolovromann.myshopping.core.ui.component.DropdownMenuTitle
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.action.SuggestionsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.model.SettingsItem
import ru.sokolovromann.myshopping.feature.settings.model.SuggestionsSettingsItems

@Composable
fun SuggestionsSettingsCard(
    onAction: (SuggestionsSettingsAction) -> Unit,
    items: SuggestionsSettingsItems
) {
    SettingsCard(stringResource(R.string.settings_text_suggestions)) {
        AddingModeItem(items.addingMode) {
            val action = SuggestionsSettingsAction.OnAddingModeSelected(it)
            onAction(action)
        }
    }
}

// ADDING MODE

@Composable
private fun AddingModeItem(
    data: SettingsItem<SuggestionAddingMode>,
    onSelected: (SuggestionAddingMode) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_suggestion_adding_mode)) },
        body = {
            Text(data.text.asCompose())
            AddingModeMenu(
                expanded = expanded,
                onSelected = { newAddingMode ->
                    expanded = false
                    newAddingMode?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun AddingModeMenu(
    expanded: Boolean,
    onSelected: (SuggestionAddingMode?) -> Unit,
    selected: SuggestionAddingMode
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_suggestion_adding_mode))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_suggestion_adding_mode_name),
            selected = selected == SuggestionAddingMode.Name,
            onClick = { onSelected(SuggestionAddingMode.Name) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_suggestion_adding_mode_all),
            selected = selected == SuggestionAddingMode.All,
            onClick = { onSelected(SuggestionAddingMode.All) }
        )

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_suggestion_adding_mode_off),
            selected = selected == SuggestionAddingMode.Off,
            onClick = { onSelected(SuggestionAddingMode.Off) }
        )
    }
}