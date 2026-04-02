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
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestionDetails
import ru.sokolovromann.myshopping.data39.suggestions.TakeSuggestions
import ru.sokolovromann.myshopping.ui.compose.event.MaxAutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.model.SelectedValue
import ru.sokolovromann.myshopping.ui.viewmodel.MaxAutocompletesViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.MaxAutocompletesEvent

@Composable
fun MaxAutocompletesScreen(
    navController: NavController,
    viewModel: MaxAutocompletesViewModel = hiltViewModel()
) {
    val state = viewModel.maxAutocompletesState

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                MaxAutocompletesScreenEvent.OnShowBackScreen -> {
                    navController.popBackStack()
                }
            }
        }
    }

    DefaultDialog(
        onDismissRequest = {
            val event = MaxAutocompletesEvent.OnClickCancel
            viewModel.onEvent(event)
        },
        header = { Text(stringResource(R.string.maxAutocompletes_header)) },
        actionButtons = {
            TextButton(
                onClick = { viewModel.onEvent(MaxAutocompletesEvent.OnClickCancel) },
                enabled = !state.waiting,
                content = { Text(stringResource(R.string.maxAutocompletes_action_cancelSavingMaxAutocompletes)) }
            )
            TextButton(
                onClick = { viewModel.onEvent(MaxAutocompletesEvent.OnClickSave) },
                enabled = !state.waiting,
                content = { Text(stringResource(R.string.maxAutocompletes_action_saveMaxAutocompletes)) }
            )
        }
    ) {
        val scrollState = rememberScrollState()
        Column(modifier = Modifier
            .scrollable(scrollState, Orientation.Vertical)
        ) {
            TakeSuggestionsItem(
                selectedValue = state.takeSuggestionsValue,
                onSelect = {
                    val event = MaxAutocompletesEvent.OnSelectTakeSuggestions(expanded = true)
                    viewModel.onEvent(event)
                },
                expanded = state.expandedTakeSuggestions,
                onDismissRequest = {
                    val event = MaxAutocompletesEvent.OnSelectTakeSuggestions(expanded = false)
                    viewModel.onEvent(event)
                },
                onSelected = {
                    val event = MaxAutocompletesEvent.OnTakeSuggestionsSelected(it)
                    viewModel.onEvent(event)
                }
            )
            Spacer(modifier = Modifier.size(MaxAutocompletesSpacerSize))
            TakeSuggestionsDetailsItem(
                selectedValue = state.takeDetailsValue,
                onSelect = {
                    val event = MaxAutocompletesEvent.OnSelectTakeDetails(expanded = true)
                    viewModel.onEvent(event)
                },
                expanded = state.expandedTakeDetails,
                onDismissRequest = {
                    val event = MaxAutocompletesEvent.OnSelectTakeDetails(expanded = false)
                    viewModel.onEvent(event)
                },
                onSelected = {
                    val event = MaxAutocompletesEvent.OnTakeDetailsSelected(it)
                    viewModel.onEvent(event)
                }
            )
        }
    }
}

@Composable
private fun TakeSuggestionsItem(
    selectedValue: SelectedValue<TakeSuggestions>,
    onSelect: () -> Unit,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSelected: (TakeSuggestions) -> Unit
) {
    val selected = selectedValue.selected

    AppItem(
        onClick = onSelect,
        title = { Text(text = stringResource(R.string.maxAutocompletes_title_takeSuggestions)) },
        body = { Text(text = selectedValue.text.asCompose()) },
        dropdownMenu = {
            AppDropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismissRequest
            ) {
                AppDropdownMenuItem(
                    onClick = { onSelected(TakeSuggestions.Few) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_takeFewSuggestions)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestions.Few) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(TakeSuggestions.Medium) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_takeMediumSuggestions)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestions.Medium) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(TakeSuggestions.DoNotTake) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_doNotTakeSuggestions)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestions.DoNotTake) }
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface
    )
}

@Composable
private fun TakeSuggestionsDetailsItem(
    selectedValue: SelectedValue<TakeSuggestionDetails>,
    onSelect: () -> Unit,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSelected: (TakeSuggestionDetails) -> Unit
) {
    val selected = selectedValue.selected

    AppItem(
        onClick = onSelect,
        title = { Text(text = stringResource(R.string.maxAutocompletes_title_takeDetails)) },
        body = { Text(text = selectedValue.text.asCompose()) },
        dropdownMenu = {
            AppDropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismissRequest
            ) {
                AppDropdownMenuItem(
                    onClick = { onSelected(TakeSuggestionDetails.Few) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_takeFewDetails)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestionDetails.Few) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(TakeSuggestionDetails.Medium) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_takeMediumDetails)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestionDetails.Medium) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(TakeSuggestionDetails.Many) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_takeManyDetails)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestionDetails.Many) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(TakeSuggestionDetails.All) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_takeAllDetails)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestionDetails.All) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(TakeSuggestionDetails.DoNotTake) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_doNotTakeDetails)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestionDetails.DoNotTake) }
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface
    )
}

private val MaxAutocompletesSpacerSize = 4.dp