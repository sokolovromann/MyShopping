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
import ru.sokolovromann.myshopping.data.model.SwipeProduct
import ru.sokolovromann.myshopping.ui.compose.event.SwipeProductScreenEvent
import ru.sokolovromann.myshopping.ui.model.SelectedValue
import ru.sokolovromann.myshopping.ui.viewmodel.SwipeProductViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.SwipeProductEvent

@Composable
fun SwipeProductScreen(
    navController: NavController,
    viewModel: SwipeProductViewModel = hiltViewModel()
) {
    val state = viewModel.swipeProductState

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                SwipeProductScreenEvent.OnShowBackScreen -> navController.popBackStack()
            }
        }
    }

    DefaultDialog(
        onDismissRequest = { viewModel.onEvent(SwipeProductEvent.OnClickCancel) },
        header = { Text(text = stringResource(R.string.swipeProduct_header)) },
        actionButtons = {
            TextButton(
                onClick = { viewModel.onEvent(SwipeProductEvent.OnClickCancel) },
                enabled = !state.waiting,
                content = { Text(text = stringResource(R.string.swipeProduct_action_cancel)) }
            )
            TextButton(
                onClick = { viewModel.onEvent(SwipeProductEvent.OnClickSave) },
                enabled = !state.waiting,
                content = { Text(text = stringResource(R.string.swipeProduct_action_save)) }
            )
        }
    ) {
        val scrollState = rememberScrollState()
        Column(modifier = Modifier
            .scrollable(scrollState, Orientation.Vertical)
        ) {
            SwipeProductItem(
                title = stringResource(R.string.swipeProduct_text_left),
                selectedValue = state.swipeProductLeftValue,
                onSelect = {
                    val event = SwipeProductEvent.OnSelectSwipeProductLeft(expanded = true)
                    viewModel.onEvent(event)
                },
                expanded = state.swipeProductLeftExpanded,
                onDismissRequest = {
                    val event = SwipeProductEvent.OnSelectSwipeProductLeft(expanded = false)
                    viewModel.onEvent(event)
                },
                onSelected = {
                    val event = SwipeProductEvent.OnSwipeProductLeftSelected(it)
                    viewModel.onEvent(event)
                }
            )

            Spacer(modifier = Modifier.size(SwipeProductSpacerSize))

            SwipeProductItem(
                title = stringResource(R.string.swipeProduct_text_right),
                selectedValue = state.swipeProductRightValue,
                onSelect = {
                    val event = SwipeProductEvent.OnSelectSwipeProductRight(expanded = true)
                    viewModel.onEvent(event)
                },
                expanded = state.swipeProductRightExpanded,
                onDismissRequest = {
                    val event = SwipeProductEvent.OnSelectSwipeProductRight(expanded = false)
                    viewModel.onEvent(event)
                },
                onSelected = {
                    val event = SwipeProductEvent.OnSwipeProductRightSelected(it)
                    viewModel.onEvent(event)
                }
            )
        }
    }
}

@Composable
private fun SwipeProductItem(
    title: String,
    selectedValue: SelectedValue<SwipeProduct>,
    onSelect: () -> Unit,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSelected: (SwipeProduct) -> Unit
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
                    onClick = { onSelected(SwipeProduct.DISABLED) },
                    text = { Text(text = stringResource(R.string.swipeProduct_action_disabled)) },
                    right = { CheckmarkAppCheckbox(checked = selected == SwipeProduct.DISABLED) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(SwipeProduct.EDIT) },
                    text = { Text(text = stringResource(R.string.swipeProduct_action_edit)) },
                    right = { CheckmarkAppCheckbox(checked = selected == SwipeProduct.EDIT) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(SwipeProduct.DELETE) },
                    text = { Text(text = stringResource(R.string.swipeProduct_action_delete)) },
                    right = { CheckmarkAppCheckbox(checked = selected == SwipeProduct.DELETE) }
                )
                AppDropdownMenuItem(
                    onClick = { onSelected(SwipeProduct.COMPLETE) },
                    text = { Text(text = stringResource(R.string.swipeProduct_action_complete)) },
                    right = { CheckmarkAppCheckbox(checked = selected == SwipeProduct.COMPLETE) }
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface
    )
}

private val SwipeProductSpacerSize = 4.dp