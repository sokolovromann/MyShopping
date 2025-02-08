package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.IconButton
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
import ru.sokolovromann.myshopping.ui.compose.event.SelectFromAutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.model.UiIcon
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.ui.viewmodel.SelectFromAutocompletesViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.SelectFromAutocompletesEvent

@Composable
fun SelectFromAutocompletesScreen(
    navController: NavController,
    viewModel: SelectFromAutocompletesViewModel = hiltViewModel()
) {
    val state = viewModel.selectFromAutocompletesState

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                SelectFromAutocompletesScreenEvent.OnShowBackScreen -> {
                    navController.popBackStack()
                }
            }
        }
    }

    BackHandler { viewModel.onEvent(SelectFromAutocompletesEvent.OnClickCancel) }

    AppScaffold(
        topBar = {
            AppTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            val event = SelectFromAutocompletesEvent.OnClickCancel
                            viewModel.onEvent(event)
                        },
                        enabled = !state.waiting
                    ) {
                        DefaultIcon(
                            icon = UiIcon.Back
                        )
                    }
                },
                actions = {
                    TextButton(
                        enabled = !state.waiting,
                        onClick = { viewModel.onEvent(SelectFromAutocompletesEvent.OnClickSave) },
                        content = { Text(stringResource(R.string.selectFromAutocompletes_action_addToShoppingList)) }
                    )
                }
            )
        },
        backgroundColor = MaterialTheme.colors.surface
    ) { paddings ->
        SmartphoneTabletAppGrid(
            modifier = Modifier.padding(paddings),
            multiColumns = state.multiColumns,
            deviceSize = state.deviceSize,
            isWaiting = state.waiting,
            notFound = { Text(text = stringResource(R.string.selectFromAutocompletes_text_autocompletesNotFound)) },
            isNotFound = state.isNotFound()
        ) {
            items(state.autocompleteNames) {
                val nameToString = it.asCompose()
                val selected = state.selectedNames?.contains(nameToString) ?: false

                AppMultiColumnsItem(
                    multiColumns = state.multiColumns,
                    title = getAutocompleteItemTitleOrNull(it),
                    right = getAutocompleteItemRightOrNull(selected),
                    onClick = {
                        val event = SelectFromAutocompletesEvent.OnAutocompleteSelected(!selected, nameToString)
                        viewModel.onEvent(event)
                    },
                    backgroundColor = getAppItemBackgroundColor(selected)
                )
            }
        }
    }
}

@Composable
private fun getAutocompleteItemTitleOrNull(
    name: UiString
) = itemOrNull(enabled = name.asCompose().isNotEmpty()) {
    Text(
        modifier = Modifier.padding(AutocompleteItemTextPaddings),
        text = name.asCompose()
    )
}

@Composable
private fun getAutocompleteItemRightOrNull(
    selected: Boolean
) = itemOrNull(enabled = selected) {
    CheckmarkAppCheckbox(
        modifier = Modifier.padding(AutocompleteItemCheckboxPaddings),
        checked = true,
        checkmarkColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    )
}

private val AutocompleteItemTextPaddings = PaddingValues(horizontal = 8.dp)
private val AutocompleteItemCheckboxPaddings = PaddingValues(start = 0.dp, end = 8.dp)