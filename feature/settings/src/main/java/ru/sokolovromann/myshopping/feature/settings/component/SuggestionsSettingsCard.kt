package ru.sokolovromann.myshopping.feature.settings.component

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionDetails
import ru.sokolovromann.myshopping.core.domain.model.DisplaySuggestionNames
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
        DisplayNamesItem(items.displayNames) {
            val action = SuggestionsSettingsAction.OnDisplayNamesSelected(it)
            onAction(action)
        }
        DisplayDetailsItem(items.displayDetails) {
            val action = SuggestionsSettingsAction.OnDisplayDetailsSelected(it)
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

// DISPLAY NAMES

@Composable
private fun DisplayNamesItem(
    data: SettingsItem<DisplaySuggestionNames>,
    onSelected: (DisplaySuggestionNames) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_display_suggestion_names)) },
        body = {
            Text(data.text.asCompose())
            DisplayNamesMenu(
                expanded = expanded,
                onSelected = { newDisplayNames ->
                    expanded = false
                    newDisplayNames?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun DisplayNamesMenu(
    expanded: Boolean,
    onSelected: (DisplaySuggestionNames?) -> Unit,
    selected: DisplaySuggestionNames
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_display_suggestion_names))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_display_suggestion_names_low),
            selected = selected == DisplaySuggestionNames.Low,
            onClick = { onSelected(DisplaySuggestionNames.Low) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_display_suggestion_names_medium),
            selected = selected == DisplaySuggestionNames.Medium,
            onClick = { onSelected(DisplaySuggestionNames.Medium) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_display_suggestion_names_do_not_display),
            selected = selected == DisplaySuggestionNames.DoNotDisplay,
            onClick = { onSelected(DisplaySuggestionNames.DoNotDisplay) }
        )
    }
}

// DISPLAY DETAILS

@Composable
private fun DisplayDetailsItem(
    data: SettingsItem<DisplaySuggestionDetails>,
    onSelected: (DisplaySuggestionDetails) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    SettingsCardItem(
        title = { Text(stringResource(R.string.settings_text_display_suggestion_details)) },
        body = {
            Text(data.text.asCompose())
            DisplayDetailsMenu(
                expanded = expanded,
                onSelected = { newDisplayDetails ->
                    expanded = false
                    newDisplayDetails?.let { onSelected(it) }
                },
                selected = data.selected
            )
        },
        onClick = { expanded = true }
    )
}

@Composable
private fun DisplayDetailsMenu(
    expanded: Boolean,
    onSelected: (DisplaySuggestionDetails?) -> Unit,
    selected: DisplaySuggestionDetails
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onSelected(null) }
    ) {
        DropdownMenuTitle(stringResource(R.string.settings_text_display_suggestion_details_menu))

        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_display_suggestion_details_low),
            selected = selected == DisplaySuggestionDetails.Low,
            onClick = { onSelected(DisplaySuggestionDetails.Low) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_display_suggestion_details_medium),
            selected = selected == DisplaySuggestionDetails.Medium,
            onClick = { onSelected(DisplaySuggestionDetails.Medium) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_display_suggestion_details_many),
            selected = selected == DisplaySuggestionDetails.Many,
            onClick = { onSelected(DisplaySuggestionDetails.Many) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_display_suggestion_details_all),
            selected = selected == DisplaySuggestionDetails.All,
            onClick = { onSelected(DisplaySuggestionDetails.All) }
        )
        DropdownMenuSelectableItem(
            text = stringResource(R.string.settings_text_display_suggestion_details_do_not_display),
            selected = selected == DisplaySuggestionDetails.DoNotDisplay,
            onClick = { onSelected(DisplaySuggestionDetails.DoNotDisplay) }
        )
    }
}