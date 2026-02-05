package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.compose.event.AddEditAutocompleteScreenEvent
import ru.sokolovromann.myshopping.ui.model.UiIcon
import ru.sokolovromann.myshopping.ui.viewmodel.AddEditAutocompleteViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.AddEditAutocompleteEvent

@Composable
fun AddEditAutocompleteScreen(
    navController: NavController,
    viewModel: AddEditAutocompleteViewModel = hiltViewModel()
) {
    val state = viewModel.addEditAutocompleteState
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                AddEditAutocompleteScreenEvent.OnShowBackScreen -> {
                    focusManager.clearFocus(force = true)
                    navController.popBackStack()
                }
                AddEditAutocompleteScreenEvent.OnShowKeyboard -> {
                    focusRequester.requestFocus()
                }
            }
        }
    }

    BackHandler { viewModel.onEvent(AddEditAutocompleteEvent.OnClickCancel) }

    AppScaffold(
        topBar = {
            AppTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.onEvent(AddEditAutocompleteEvent.OnClickCancel) },
                        content = { DefaultIcon(icon = UiIcon.Back) }
                    )
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.onEvent(AddEditAutocompleteEvent.OnClickSave) },
                        content = { Text(stringResource(R.string.addEditAutocomplete_action_saveAutocomplete)) }
                    )
                }
            )
        },
        backgroundColor = MaterialTheme.colors.surface
    ) { paddings ->
        AddEditAutocompletesContent(scaffoldPaddings = paddings) {
            OutlinedAppTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = state.nameValue,
                onValueChange = {
                    val event = AddEditAutocompleteEvent.OnNameValueChanged(it)
                    viewModel.onEvent(event)
                },
                label = {
                    Text(text = stringResource(R.string.addEditAutocomplete_label_name))
                },
                error = { Text(text = stringResource(R.string.addEditAutocomplete_message_nameError)) },
                showError = state.nameError,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { viewModel.onEvent(AddEditAutocompleteEvent.OnClickSave) }
                )
            )
            Spacer(modifier = Modifier.size(AddEditAutocompleteSpacerMediumSize))
            AddEditAutocompleteChips(
                title = stringResource(R.string.autocompletes_body_brands),
                enabled = state.brands.isNotEmpty()
            ) {
                state.brands.forEach {
                    AddEditAutocompleteChipContent(
                        text = { Text(it.first) },
                        onClickDelete = {
                            val event = AddEditAutocompleteEvent.OnClickDeleteDetail(it.second)
                            viewModel.onEvent(event)
                        }
                    )
                }
            }
            AddEditAutocompleteChips(
                title = stringResource(R.string.autocompletes_body_sizes),
                enabled = state.sizes.isNotEmpty()
            ) {
                state.sizes.forEach {
                    AddEditAutocompleteChipContent(
                        text = { Text(it.first) },
                        onClickDelete = {
                            val event = AddEditAutocompleteEvent.OnClickDeleteDetail(it.second)
                            viewModel.onEvent(event)
                        }
                    )
                }
            }
            AddEditAutocompleteChips(
                title = stringResource(R.string.autocompletes_body_colors),
                enabled = state.colors.isNotEmpty()
            ) {
                state.colors.forEach {
                    AddEditAutocompleteChipContent(
                        text = { Text(it.first) },
                        onClickDelete = {
                            val event = AddEditAutocompleteEvent.OnClickDeleteDetail(it.second)
                            viewModel.onEvent(event)
                        }
                    )
                }
            }
            AddEditAutocompleteChips(
                title = stringResource(R.string.autocompletes_body_manufacturers),
                enabled = state.manufacturers.isNotEmpty()
            ) {
                state.manufacturers.forEach {
                    AddEditAutocompleteChipContent(
                        text = { Text(it.first) },
                        onClickDelete = {
                            val event = AddEditAutocompleteEvent.OnClickDeleteDetail(it.second)
                            viewModel.onEvent(event)
                        }
                    )
                }
            }

            AddEditAutocompleteChips(
                title = stringResource(R.string.autocompletes_body_quantities),
                enabled = state.quantities.isNotEmpty()
            ) {
                state.quantities.forEach {
                    AddEditAutocompleteChipContent(
                        text = { Text(it.first) },
                        onClickDelete = {
                            val event = AddEditAutocompleteEvent.OnClickDeleteDetail(it.second)
                            viewModel.onEvent(event)
                        }
                    )
                }
            }

            if (state.displayMoney) {
                AddEditAutocompleteChips(
                    title = stringResource(R.string.autocompletes_body_prices),
                    enabled = state.prices.isNotEmpty()
                ) {
                    state.prices.forEach {
                        AddEditAutocompleteChipContent(
                            text = { Text(it.first) },
                            onClickDelete = {
                                val event = AddEditAutocompleteEvent.OnClickDeleteDetail(it.second)
                                viewModel.onEvent(event)
                            }
                        )
                    }
                }
                AddEditAutocompleteChips(
                    title = stringResource(R.string.autocompletes_body_discounts),
                    enabled = state.discounts.isNotEmpty()
                ) {
                    state.discounts.forEach {
                        AddEditAutocompleteChipContent(
                            text = { Text(it.first) },
                            onClickDelete = {
                                val event = AddEditAutocompleteEvent.OnClickDeleteDetail(it.second)
                                viewModel.onEvent(event)
                            }
                        )
                    }
                }
                AddEditAutocompleteChips(
                    title = stringResource(R.string.autocompletes_body_totals),
                    enabled = state.totals.isNotEmpty()
                ) {
                    state.totals.forEach {
                        AddEditAutocompleteChipContent(
                            text = { Text(it.first) },
                            onClickDelete = {
                                val event = AddEditAutocompleteEvent.OnClickDeleteDetail(it.second)
                                viewModel.onEvent(event)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddEditAutocompletesContent(
    scaffoldPaddings: PaddingValues,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(scaffoldPaddings)
            .padding(AddEditAutocompleteContentPaddings)
    ) {
        content()
        Spacer(modifier = Modifier.height(AddEditAutocompleteSpacerHeight))
    }
}

@Composable
private fun AddEditAutocompleteChips(
    title: String,
    enabled: Boolean,
    content: @Composable (RowScope.() -> Unit)
) {
    if (!enabled) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = ButtonDefaults.OutlinedBorderSize,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                shape = MaterialTheme.shapes.small
            )
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier.padding(
                vertical = 4.dp,
                horizontal = 16.dp
            )
        ) {
            Text(
                text = title.replace(oldValue = ": %s", newValue = ""),
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(modifier = Modifier.size(AddEditAutocompleteSpacerMediumSize))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.horizontalScroll(scrollState),
                content = content
            )
        }
    }
}

@Composable
private fun AddEditAutocompleteChipContent(
    text: @Composable (() -> Unit),
    onClickDelete: () -> Unit
) {
    AppChip(
        onClick = {},
        content = {
            text()
            Spacer(modifier = Modifier.size(AddEditAutocompleteSpacerMediumSize))
            AppVerticalDivider(height = 16.dp)
            Spacer(modifier = Modifier.size(AddEditAutocompleteSpacerMediumSize))
            IconButton(
                modifier = Modifier.size(16.dp),
                onClick = onClickDelete,
                content = { DefaultIcon(icon = UiIcon.Clear) }
            )
        }
    )
    Spacer(modifier = Modifier.size(AddEditAutocompleteSpacerMediumSize))
}

private val AddEditAutocompleteContentPaddings = PaddingValues(
    horizontal = 8.dp,
    vertical = 4.dp
)

private val AddEditAutocompleteSpacerMediumSize = 4.dp
private val AddEditAutocompleteSpacerHeight = 128.dp