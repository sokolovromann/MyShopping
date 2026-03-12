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
                selectedValue = state.takeNamesValue,
                onSelect = {
                    val event = MaxAutocompletesEvent.OnSelectTakeNames(expanded = true)
                    viewModel.onEvent(event)
                },
                expanded = state.expandedTakeNames,
                onDismissRequest = {
                    val event = MaxAutocompletesEvent.OnSelectTakeNames(expanded = false)
                    viewModel.onEvent(event)
                },
                onSelected = {
                    val event = MaxAutocompletesEvent.OnTakeNamesSelected(it)
                    viewModel.onEvent(event)
                }
            )
            Spacer(modifier = Modifier.size(MaxAutocompletesSpacerSize))
            TakeSuggestionsDetailsItem(
                title = stringResource(R.string.maxAutocompletes_title_takeDetailsDescriptions),
                selectedValue = state.takeDetailsDescriptions,
                onSelect = {
                    val event = MaxAutocompletesEvent.OnSelectTakeDetailsDescriptions(expanded = true)
                    viewModel.onEvent(event)
                },
                expanded = state.expandedTakeDetailsDescriptions,
                onDismissRequest = {
                    val event = MaxAutocompletesEvent.OnSelectTakeDetailsDescriptions(expanded = false)
                    viewModel.onEvent(event)
                },
                onSelected = {
                    val event = MaxAutocompletesEvent.OnTakeDetailsDescriptionsSelected(it)
                    viewModel.onEvent(event)
                }
            )
            Spacer(modifier = Modifier.size(MaxAutocompletesSpacerSize))
            TakeSuggestionsDetailsItem(
                title = stringResource(R.string.maxAutocompletes_title_takeDetailsQuantities),
                selectedValue = state.takeDetailsQuantities,
                onSelect = {
                    val event = MaxAutocompletesEvent.OnSelectTakeDetailsQuantities(expanded = true)
                    viewModel.onEvent(event)
                },
                expanded = state.expandedTakeDetailsQuantities,
                onDismissRequest = {
                    val event = MaxAutocompletesEvent.OnSelectTakeDetailsQuantities(expanded = false)
                    viewModel.onEvent(event)
                },
                onSelected = {
                    val event = MaxAutocompletesEvent.OnTakeDetailsQuantitiesSelected(it)
                    viewModel.onEvent(event)
                }
            )
            Spacer(modifier = Modifier.size(MaxAutocompletesSpacerSize))
            TakeSuggestionsDetailsItem(
                title = stringResource(R.string.maxAutocompletes_title_takeDetailsMoney),
                selectedValue = state.takeDetailsMoney,
                onSelect = {
                    val event = MaxAutocompletesEvent.OnSelectTakeDetailsMoney(expanded = true)
                    viewModel.onEvent(event)
                },
                expanded = state.expandedTakeDetailsMoney,
                onDismissRequest = {
                    val event = MaxAutocompletesEvent.OnSelectTakeDetailsMoney(expanded = false)
                    viewModel.onEvent(event)
                },
                onSelected = {
                    val event = MaxAutocompletesEvent.OnTakeDetailsMoneySelected(it)
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
                    onClick = { onSelected(TakeSuggestions.DoNotTake) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_doNotTakeSuggestions)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestions.DoNotTake) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(TakeSuggestions.Five) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_takeFiveSuggestions)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestions.Five) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(TakeSuggestions.Ten) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_takeTenSuggestions)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestions.Ten) }
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface
    )
}

@Composable
private fun TakeSuggestionsDetailsItem(
    title: String,
    selectedValue: SelectedValue<TakeSuggestionDetails>,
    onSelect: () -> Unit,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSelected: (TakeSuggestionDetails) -> Unit
) {
    val selected = selectedValue.selected

    AppItem(
        onClick = onSelect,
        title = { Text(text = title) },
        body = { Text(text = selectedValue.text.asCompose()) },
        dropdownMenu = {
            AppDropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismissRequest
            ) {
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
                AppDropdownMenuItem(
                    onClick = { onSelected(TakeSuggestionDetails.One) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_takeOneDetails)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestionDetails.One) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(TakeSuggestionDetails.Three) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_takeThreeDetails)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestionDetails.Three) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(TakeSuggestionDetails.Five) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_takeFiveDetails)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestionDetails.Five) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(TakeSuggestionDetails.Ten) },
                    text = { Text(text = stringResource(R.string.maxAutocompletes_body_takeTenDetails)) },
                    right = { CheckmarkAppCheckbox(checked = selected == TakeSuggestionDetails.Ten) }
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface
    )
}

private val MaxAutocompletesSpacerSize = 4.dp