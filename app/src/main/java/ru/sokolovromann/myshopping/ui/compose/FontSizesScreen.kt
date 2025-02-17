package ru.sokolovromann.myshopping.ui.compose

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.FontSize
import ru.sokolovromann.myshopping.ui.compose.event.FontSizesScreenEvent
import ru.sokolovromann.myshopping.ui.model.SelectedValue
import ru.sokolovromann.myshopping.ui.viewmodel.FontSizesViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.FontSizesEvent

@Composable
fun FontSizeScreen(
    navController: NavController,
    viewModel: FontSizesViewModel = hiltViewModel()
) {
    val state = viewModel.fontSizesState

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                FontSizesScreenEvent.OnShowBackScreen -> navController.popBackStack()
            }
        }
    }

    DefaultDialog(
        onDismissRequest = { viewModel.onEvent(FontSizesEvent.OnClickCancel) },
        header = { Text(text = stringResource(R.string.fontSizes_header)) },
        actionButtons = {
            TextButton(
                onClick = { viewModel.onEvent(FontSizesEvent.OnClickCancel) },
                enabled = !state.waiting,
                content = { Text(text = stringResource(R.string.fontSizes_action_cancelSavingFontSize)) }
            )
            TextButton(
                onClick = { viewModel.onEvent(FontSizesEvent.OnClickSave) },
                enabled = !state.waiting,
                content = { Text(text = stringResource(R.string.fontSizes_action_saveFontSize)) }
            )
        }
    ) {
        val scrollState = rememberScrollState()
        Column(modifier = Modifier
            .scrollable(scrollState, Orientation.Vertical)
        ) {
            FontSizeItem(
                title = stringResource(R.string.fontSizes_text_appFontSize),
                selectedFontSize = state.appFontSizeValue,
                onSelect = {
                    val event = FontSizesEvent.OnSelectAppFontSize(expanded = true)
                    viewModel.onEvent(event)
                },
                expanded = state.expandedAppFontSize,
                onDismissRequest = {
                    val event = FontSizesEvent.OnSelectAppFontSize(expanded = false)
                    viewModel.onEvent(event)
                },
                onSelected = {
                    val event = FontSizesEvent.OnAppFontSizeSelected(it)
                    viewModel.onEvent(event)
                }
            )

            Spacer(modifier = Modifier.size(FontSizesSpacerSize))

            FontSizeItem(
                title = stringResource(R.string.fontSizes_text_widgetFontSize),
                selectedFontSize = state.widgetFontSizeValue,
                onSelect = {
                    val event = FontSizesEvent.OnSelectWidgetFontSize(expanded = true)
                    viewModel.onEvent(event)
                },
                expanded = state.expandedWidgetFontSize,
                onDismissRequest = {
                    val event = FontSizesEvent.OnSelectWidgetFontSize(expanded = false)
                    viewModel.onEvent(event)
                },
                onSelected = {
                    val event = FontSizesEvent.OnWidgetFontSizeSelected(it)
                    viewModel.onEvent(event)
                }
            )
        }
    }
}

@Composable
private fun FontSizeItem(
    title: String,
    selectedFontSize: SelectedValue<FontSize>,
    onSelect: () -> Unit,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSelected: (FontSize) -> Unit
) {
    val selected = selectedFontSize.selected

    AppItem(
        onClick = onSelect,
        title = { Text(text = title) },
        body = { Text(text = selectedFontSize.text.asCompose()) },
        dropdownMenu = {
            AppDropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismissRequest
            ) {
                AppDropdownMenuItem(
                    onClick = { onSelected(FontSize.SMALL) },
                    text = { Text(text = stringResource(R.string.fontSizes_action_selectSmallFontSize)) },
                    right = { CheckmarkAppCheckbox(checked = selected == FontSize.SMALL) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(FontSize.MEDIUM) },
                    text = { Text(text = stringResource(R.string.fontSizes_action_selectMediumFontSize)) },
                    right = { CheckmarkAppCheckbox(checked = selected == FontSize.MEDIUM) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(FontSize.LARGE) },
                    text = { Text(text = stringResource(R.string.fontSizes_action_selectLargeFontSize)) },
                    right = { CheckmarkAppCheckbox(checked = selected == FontSize.LARGE) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(FontSize.VERY_LARGE) },
                    text = { Text(text = stringResource(R.string.fontSizes_action_selectVeryLargeFontSize)) },
                    right = { CheckmarkAppCheckbox(checked = selected == FontSize.VERY_LARGE) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(FontSize.HUGE) },
                    text = { Text(text = stringResource(R.string.fontSizes_action_selectHugeFontSize)) },
                    right = { CheckmarkAppCheckbox(checked = selected == FontSize.HUGE) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(FontSize.VERY_HUGE) },
                    text = { Text(text = stringResource(R.string.fontSizes_action_selectVeryHugeFontSize)) },
                    right = { CheckmarkAppCheckbox(checked = selected == FontSize.VERY_HUGE) }
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface
    )
}

private val FontSizesSpacerSize = 4.dp