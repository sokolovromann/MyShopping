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
import ru.sokolovromann.myshopping.data.model.DisplayCompleted
import ru.sokolovromann.myshopping.ui.compose.event.DisplayCompletedScreenEvent
import ru.sokolovromann.myshopping.ui.model.SelectedValue
import ru.sokolovromann.myshopping.ui.viewmodel.DisplayCompletedViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.DisplayCompletedEvent

@Composable
fun DisplayCompletedScreen(
    navController: NavController,
    viewModel: DisplayCompletedViewModel = hiltViewModel()
) {

    val state = viewModel.displayCompletedState

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                DisplayCompletedScreenEvent.OnShowBackScreen -> navController.popBackStack()
            }
        }
    }

    DefaultDialog(
        onDismissRequest = { viewModel.onEvent(DisplayCompletedEvent.OnClickCancel) },
        header = { Text(text = stringResource(R.string.displayCompleted_header)) },
        actionButtons = {
            TextButton(
                onClick = { viewModel.onEvent(DisplayCompletedEvent.OnClickCancel) },
                enabled = !state.waiting,
                content = { Text(text = stringResource(R.string.displayCompleted_action_cancelSavingDisplayCompleted)) }
            )
            TextButton(
                onClick = { viewModel.onEvent(DisplayCompletedEvent.OnClickSave) },
                enabled = !state.waiting,
                content = { Text(text = stringResource(R.string.displayCompleted_action_saveDisplayCompleted)) }
            )
        }
    ) {
        val scrollState = rememberScrollState()
        Column(modifier = Modifier
            .scrollable(scrollState, Orientation.Vertical)
        ) {
            DisplayCompletedItem(
                title = stringResource(R.string.displayCompleted_text_appDisplayCompleted),
                selectedDisplayCompleted = state.appDisplayCompletedValue,
                onSelect = {
                    val event = DisplayCompletedEvent.OnSelectAppDisplayCompleted(expanded = true)
                    viewModel.onEvent(event)
                },
                expanded = state.expandedAppDisplayCompleted,
                onDismissRequest = {
                    val event = DisplayCompletedEvent.OnSelectAppDisplayCompleted(expanded = false)
                    viewModel.onEvent(event)
                },
                onSelected = {
                    val event = DisplayCompletedEvent.OnAppDisplayCompletedSelected(it)
                    viewModel.onEvent(event)
                },
            )

            Spacer(modifier = Modifier.size(DisplayCompletedSpacerSize))

            DisplayCompletedItem(
                title = stringResource(R.string.displayCompleted_text_widgetDisplayCompleted),
                selectedDisplayCompleted = state.widgetDisplayCompletedValue,
                onSelect = {
                    val event = DisplayCompletedEvent.OnSelectWidgetDisplayCompleted(expanded = true)
                    viewModel.onEvent(event)
                },
                expanded = state.expandedWidgetDisplayCompleted,
                onDismissRequest = {
                    val event = DisplayCompletedEvent.OnSelectWidgetDisplayCompleted(expanded = false)
                    viewModel.onEvent(event)
                },
                onSelected = {
                    val event = DisplayCompletedEvent.OnWidgetDisplayCompletedSelected(it)
                    viewModel.onEvent(event)
                }
            )
        }
    }
}

@Composable
private fun DisplayCompletedItem(
    title: String,
    selectedDisplayCompleted: SelectedValue<DisplayCompleted>,
    onSelect: () -> Unit,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSelected: (DisplayCompleted) -> Unit
) {
    val selected = selectedDisplayCompleted.selected

    AppItem(
        onClick = onSelect,
        title = { Text(text = title) },
        body = { Text(text = selectedDisplayCompleted.text.asCompose()) },
        dropdownMenu = {
            AppDropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismissRequest
            ) {
                AppDropdownMenuItem(
                    onClick = { onSelected(DisplayCompleted.FIRST) },
                    text = { Text(text = stringResource(R.string.displayCompleted_action_displayCompletedFirst)) },
                    right = { CheckmarkAppCheckbox(checked = selected == DisplayCompleted.FIRST) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(DisplayCompleted.LAST) },
                    text = { Text(text = stringResource(R.string.displayCompleted_action_displayCompletedLast)) },
                    right = { CheckmarkAppCheckbox(checked = selected == DisplayCompleted.LAST) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(DisplayCompleted.HIDE) },
                    text = { Text(text = stringResource(R.string.displayCompleted_action_hideCompleted)) },
                    right = { CheckmarkAppCheckbox(checked = selected == DisplayCompleted.HIDE) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(DisplayCompleted.NO_SPLIT) },
                    text = { Text(text = stringResource(R.string.displayCompleted_action_noSplitCompleted)) },
                    right = { CheckmarkAppCheckbox(checked = selected == DisplayCompleted.NO_SPLIT) }
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface
    )
}

private val DisplayCompletedSpacerSize = 4.dp