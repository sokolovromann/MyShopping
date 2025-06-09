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
import ru.sokolovromann.myshopping.data.model.SwipeShopping
import ru.sokolovromann.myshopping.ui.compose.event.SwipeShoppingScreenEvent
import ru.sokolovromann.myshopping.ui.model.SelectedValue
import ru.sokolovromann.myshopping.ui.viewmodel.SwipeShoppingViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.SwipeShoppingEvent

@Composable
fun SwipeShoppingScreen(
    navController: NavController,
    viewModel: SwipeShoppingViewModel = hiltViewModel()
) {
    val state = viewModel.swipeShoppingState

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                SwipeShoppingScreenEvent.OnShowBackScreen -> navController.popBackStack()
            }
        }
    }

    DefaultDialog(
        onDismissRequest = { viewModel.onEvent(SwipeShoppingEvent.OnClickCancel) },
        header = { Text(text = stringResource(R.string.swipeShopping_header)) },
        actionButtons = {
            TextButton(
                onClick = { viewModel.onEvent(SwipeShoppingEvent.OnClickCancel) },
                enabled = !state.waiting,
                content = { Text(text = stringResource(R.string.swipeShopping_action_cancel)) }
            )
            TextButton(
                onClick = { viewModel.onEvent(SwipeShoppingEvent.OnClickSave) },
                enabled = !state.waiting,
                content = { Text(text = stringResource(R.string.swipeShopping_action_save)) }
            )
        }
    ) {
        val scrollState = rememberScrollState()
        Column(modifier = Modifier
            .scrollable(scrollState, Orientation.Vertical)
        ) {
            SwipeShoppingItem(
                title = stringResource(R.string.swipeShopping_text_left),
                selectedValue = state.swipeShoppingLeftValue,
                onSelect = {
                    val event = SwipeShoppingEvent.OnSelectSwipeShoppingLeft(expanded = true)
                    viewModel.onEvent(event)
                },
                expanded = state.swipeShoppingLeftExpanded,
                onDismissRequest = {
                    val event = SwipeShoppingEvent.OnSelectSwipeShoppingLeft(expanded = false)
                    viewModel.onEvent(event)
                },
                onSelected = {
                    val event = SwipeShoppingEvent.OnSwipeShoppingLeftSelected(it)
                    viewModel.onEvent(event)
                }
            )

            Spacer(modifier = Modifier.size(SwipeShoppingSpacerSize))

            SwipeShoppingItem(
                title = stringResource(R.string.swipeShopping_text_right),
                selectedValue = state.swipeShoppingRightValue,
                onSelect = {
                    val event = SwipeShoppingEvent.OnSelectSwipeShoppingRight(expanded = true)
                    viewModel.onEvent(event)
                },
                expanded = state.swipeShoppingRightExpanded,
                onDismissRequest = {
                    val event = SwipeShoppingEvent.OnSelectSwipeShoppingRight(expanded = false)
                    viewModel.onEvent(event)
                },
                onSelected = {
                    val event = SwipeShoppingEvent.OnSwipeShoppingRightSelected(it)
                    viewModel.onEvent(event)
                }
            )
        }
    }
}

@Composable
private fun SwipeShoppingItem(
    title: String,
    selectedValue: SelectedValue<SwipeShopping>,
    onSelect: () -> Unit,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSelected: (SwipeShopping) -> Unit
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
                    onClick = { onSelected(SwipeShopping.DISABLED) },
                    text = { Text(text = stringResource(R.string.swipeShopping_action_disabled)) },
                    right = { CheckmarkAppCheckbox(checked = selected == SwipeShopping.DISABLED) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(SwipeShopping.ARCHIVE) },
                    text = { Text(text = stringResource(R.string.swipeShopping_action_archive)) },
                    right = { CheckmarkAppCheckbox(checked = selected == SwipeShopping.ARCHIVE) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(SwipeShopping.DELETE) },
                    text = { Text(text = stringResource(R.string.swipeShopping_action_delete)) },
                    right = { CheckmarkAppCheckbox(checked = selected == SwipeShopping.DELETE) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(SwipeShopping.DELETE_PRODUCTS) },
                    text = { Text(text = stringResource(R.string.swipeShopping_action_deleteProducts)) },
                    right = { CheckmarkAppCheckbox(checked = selected == SwipeShopping.DELETE_PRODUCTS) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(SwipeShopping.COMPLETE) },
                    text = { Text(text = stringResource(R.string.swipeShopping_action_completed)) },
                    right = { CheckmarkAppCheckbox(checked = selected == SwipeShopping.COMPLETE) }
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface
    )
}

private val SwipeShoppingSpacerSize = 4.dp