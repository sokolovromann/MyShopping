package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.ShoppingPeriod
import ru.sokolovromann.myshopping.ui.DrawerScreen
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.ArchiveScreenEvent
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.viewmodel.ArchiveViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.ArchiveEvent

@Composable
fun ArchiveScreen(
    navController: NavController,
    viewModel: ArchiveViewModel = hiltViewModel()
) {
    val state = viewModel.archiveState
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                ArchiveScreenEvent.OnShowBackScreen -> {
                    navController.popBackStack()
                }

                is ArchiveScreenEvent.OnShowProductsScreen -> navController.navigate(
                    route = UiRoute.Products.productsScreen(it.shoppingUid)
                )

                is ArchiveScreenEvent.OnDrawerScreenSelected -> {
                    navController.navigateWithDrawerOption(route = it.drawerScreen.getScreen())
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                is ArchiveScreenEvent.OnSelectDrawerScreen -> coroutineScope.launch {
                    if (it.display) {
                        scaffoldState.drawerState.open()
                    } else {
                        scaffoldState.drawerState.close()
                    }
                }

                is ArchiveScreenEvent.OnHideKeyboard -> {
                    focusManager.clearFocus(force = true)
                }
            }
        }
    }

    BackHandler {
        if (scaffoldState.drawerState.isOpen) {
            viewModel.onEvent(ArchiveEvent.OnSelectDrawerScreen(false))
        } else {
            if (state.selectedUids == null) {
                if (state.displaySearch) {
                    viewModel.onEvent(ArchiveEvent.OnInvertSearch)
                } else {
                    viewModel.onEvent(ArchiveEvent.OnClickBack)
                }
            } else {
                viewModel.onEvent(ArchiveEvent.OnAllShoppingListsSelected(false))
            }
        }
    }

    AppScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (state.selectedUids == null) {
                AppTopAppBar(
                    title = { Text(text = stringResource(R.string.shoppingLists_header_archive)) },
                    navigationIcon = {
                        if (state.displaySearch) {
                            ShoppingListsCancelSearchButton { viewModel.onEvent(ArchiveEvent.OnInvertSearch) }
                        } else {
                            ShoppingListsOpenNavigationButton {
                                val event = ArchiveEvent.OnSelectDrawerScreen(display = true)
                                viewModel.onEvent(event)
                            }
                        }
                    }
                )
            } else {
                AppTopAppBar(
                    title = { Text(text = state.selectedUids?.size.toString()) },
                    navigationIcon = {
                        ShoppingListsCancelSelectionButton {
                            val event = ArchiveEvent.OnAllShoppingListsSelected(selected = false)
                            viewModel.onEvent(event)
                        }
                    },
                    actions = {
                        ShoppingListsUnarchiveDataButton {
                            val event = ArchiveEvent.OnMoveShoppingListSelected(ShoppingLocation.PURCHASES)
                            viewModel.onEvent(event)
                        }
                        ShoppingListsDeleteDataButton {
                            val event = ArchiveEvent.OnMoveShoppingListSelected(ShoppingLocation.TRASH)
                            viewModel.onEvent(event)
                        }
                        ShoppingListsSelectAllDataButton {
                            val event = ArchiveEvent.OnAllShoppingListsSelected(true)
                            viewModel.onEvent(event)
                        }
                    }
                )
            }
        },
        bottomBar = {
            AppBottomAppBar(
                content = {
                    if (state.displaySearch) {
                        OutlinedAppTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(ShoppingListsSearchPaddings)
                                .focusRequester(focusRequester),
                            value = state.searchValue,
                            onValueChange = {
                                val event = ArchiveEvent.OnSearchValueChanged(it)
                                viewModel.onEvent(event)
                            },
                            label = { Text(text = stringResource(R.string.shoppingLists_label_search)) },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(onSearch = { viewModel.onEvent(ArchiveEvent.OnClickSearchShoppingLists) })
                        )

                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    } else {
                        val totalValue = state.totalValue ?: return@AppBottomAppBar
                        if (totalValue.text.isNotEmpty()) {
                            ShoppingListsTotalContent(
                                displayTotal = totalValue.selected,
                                totalText = totalValue.text,
                                expanded = state.expandedDisplayTotal,
                                onExpanded = { viewModel.onEvent(ArchiveEvent.OnSelectDisplayTotal(it)) },
                                onSelected = {
                                    val event = ArchiveEvent.OnDisplayTotalSelected(it)
                                    viewModel.onEvent(event)
                                }
                            )
                        }
                    }
                },
                actionButtons = {
                    if (state.selectedUids == null) {
                        if (state.displaySearch) {
                            return@AppBottomAppBar
                        }

                        IconButton(onClick = { viewModel.onEvent(ArchiveEvent.OnShowArchiveMenu(true)) }) {
                            MoreIcon(contentDescription = UiString.FromResources(R.string.shoppingLists_contentDescription_archiveMenuIcon))
                            AppDropdownMenu(
                                expanded = state.expandedArchiveMenu,
                                onDismissRequest = { viewModel.onEvent(ArchiveEvent.OnShowArchiveMenu(false)) }
                            ) {
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ArchiveEvent.OnInvertMultiColumns) },
                                    text = { Text(text = state.multiColumnsValue.text.asCompose()) }
                                )
                                AppDropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_displayProducts)) },
                                    right = { MoreMenuIcon() },
                                    onClick = { viewModel.onEvent(ArchiveEvent.OnSelectDisplayProducts(true)) }
                                )
                                AppDropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_sort)) },
                                    right = { MoreMenuIcon() },
                                    onClick = { viewModel.onEvent(ArchiveEvent.OnSelectSort(true)) }
                                )
                                AppDropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_search)) },
                                    onClick = { viewModel.onEvent(ArchiveEvent.OnInvertSearch) }
                                )
                            }

                            ShoppingListsDisplayProductsMenu(
                                expanded = state.expandedDisplayProducts,
                                displayProducts = state.displayProducts,
                                onDismissRequest = {
                                    val event = ArchiveEvent.OnSelectDisplayProducts(false)
                                    viewModel.onEvent(event)
                                },
                                onSelected = {
                                    val event = ArchiveEvent.OnDisplayProductsSelected(it)
                                    viewModel.onEvent(event)
                                }
                            )
                            ShoppingListsSortByMenu(
                                expanded = state.expandedSort,
                                sortValue = state.sortValue,
                                sortFormatted = state.sortFormatted,
                                onDismissRequest = { viewModel.onEvent(ArchiveEvent.OnSelectSort(false)) },
                                onSelected = {
                                    val event = ArchiveEvent.OnSortSelected(it)
                                    viewModel.onEvent(event)
                                },
                                onReverse = { viewModel.onEvent(ArchiveEvent.OnReverseSort) },
                                onInvertSortFormatted = { viewModel.onEvent(ArchiveEvent.OnInvertSortFormatted) }
                            )
                        }
                    }
                }
            )
        },
        drawerContent = {
            AppDrawerContent(
                selected = DrawerScreen.ARCHIVE.toUiRoute(),
                onItemClick = {
                    val event = ArchiveEvent.OnDrawerScreenSelected(it.toDrawerScreen())
                    viewModel.onEvent(event)
                }
            )
        }
    ) { paddings ->
        ShoppingListsGrid(
            modifier = Modifier.padding(paddings),
            multiColumns = state.multiColumnsValue.selected,
            deviceSize = state.deviceSize,
            otherItems = state.shoppingLists,
            displayProducts = state.displayProducts,
            displayCompleted = state.displayCompleted,
            strikethroughCompletedProducts = state.strikethroughCompletedProducts,
            coloredCheckbox = state.coloredCheckbox,
            topBar = {
                TextButton(
                    modifier = Modifier.padding(ArchiveGridBarPaddings),
                    onClick = {
                        val event = ArchiveEvent.OnSelectArchivePeriod(expanded = true)
                        viewModel.onEvent(event)
                    }
                ) {
                    Text(text = state.archivePeriod.text.asCompose())

                    AppDropdownMenu(
                        expanded = state.expandedArchivePeriod,
                        onDismissRequest = {
                            val event = ArchiveEvent.OnSelectArchivePeriod(expanded = false)
                            viewModel.onEvent(event)
                        },
                        header = { Text(text = stringResource(R.string.shoppingLists_header_period)) }
                    ) {
                        AppDropdownMenuItem(
                            onClick = {
                                val event = ArchiveEvent.OnArchivePeriodSelected(ShoppingPeriod.ONE_MONTH)
                                viewModel.onEvent(event)
                            },
                            text = { Text(text = stringResource(R.string.shoppingLists_action_selectOneMonthPeriod)) },
                            right = { CheckmarkAppCheckbox(checked = state.archivePeriod.selected == ShoppingPeriod.ONE_MONTH) }
                        )
                        AppDropdownMenuItem(
                            onClick = {
                                val event = ArchiveEvent.OnArchivePeriodSelected(ShoppingPeriod.THREE_MONTHS)
                                viewModel.onEvent(event)
                            },
                            text = { Text(text = stringResource(R.string.shoppingLists_action_selectThreeMonthsPeriod)) },
                            right = { CheckmarkAppCheckbox(checked = state.archivePeriod.selected == ShoppingPeriod.THREE_MONTHS) }
                        )
                        AppDropdownMenuItem(
                            onClick = {
                                val event = ArchiveEvent.OnArchivePeriodSelected(ShoppingPeriod.SIX_MONTHS)
                                viewModel.onEvent(event)
                            },
                            text = { Text(text = stringResource(R.string.shoppingLists_action_selectSixMonthsPeriod)) },
                            right = { CheckmarkAppCheckbox(checked = state.archivePeriod.selected == ShoppingPeriod.SIX_MONTHS) }
                        )
                        AppDropdownMenuItem(
                            onClick = {
                                val event = ArchiveEvent.OnArchivePeriodSelected(ShoppingPeriod.ONE_YEAR)
                                viewModel.onEvent(event)
                            },
                            text = { Text(text = stringResource(R.string.shoppingLists_action_selectOneYearPeriod)) },
                            right = { CheckmarkAppCheckbox(checked = state.archivePeriod.selected == ShoppingPeriod.ONE_YEAR) }
                        )
                        AppDropdownMenuItem(
                            onClick = {
                                val event = ArchiveEvent.OnArchivePeriodSelected(ShoppingPeriod.ALL_TIME)
                                viewModel.onEvent(event)
                            },
                            text = { Text(text = stringResource(R.string.shoppingLists_action_selectAllTimePeriod)) },
                            right = { CheckmarkAppCheckbox(checked = state.archivePeriod.selected == ShoppingPeriod.ALL_TIME) }
                        )
                    }
                }
            },
            bottomBar = {
                if (state.displayHiddenShoppingLists) {
                    ShoppingListsHiddenContent {
                        viewModel.onEvent(ArchiveEvent.OnShowHiddenShoppingLists(true))
                    }
                }
            },
            isWaiting = state.waiting,
            notFound = {
                Text(
                    text = state.notFoundText.asCompose(),
                    textAlign = TextAlign.Center
                )
            },
            isNotFound = state.isNotFound(),
            onClick = {
                val uids = state.selectedUids
                val event = if (uids == null) {
                    ArchiveEvent.OnClickShoppingList(it)
                } else {
                    ArchiveEvent.OnShoppingListSelected(
                        selected = !uids.contains(it),
                        uid = it
                    )
                }
                viewModel.onEvent(event)
            },
            onLongClick = {
                if (state.selectedUids == null) {
                    val event = ArchiveEvent.OnShoppingListSelected(
                        selected = true,
                        uid = it
                    )
                    viewModel.onEvent(event)
                }
            },
            selectedUids = state.selectedUids
        )
    }
}

private val ArchiveGridBarPaddings = PaddingValues(horizontal = 8.dp)