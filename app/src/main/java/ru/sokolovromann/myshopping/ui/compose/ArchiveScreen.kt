package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.ShoppingLocation
import ru.sokolovromann.myshopping.data.model.SortBy
import ru.sokolovromann.myshopping.ui.DrawerScreen
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.ArchiveScreenEvent
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
                    title = { Text(text = stringResource(R.string.archive_header)) },
                    navigationIcon = {
                        if (state.displaySearch) {
                            IconButton(onClick = { viewModel.onEvent(ArchiveEvent.OnInvertSearch) }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.shoppingLists_contentDescription_cancelSearchingProducts)
                                )
                            }
                        } else {
                            IconButton(onClick = { viewModel.onEvent(ArchiveEvent.OnSelectDrawerScreen(true)) }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = stringResource(R.string.archive_contentDescription_navigationIcon)
                                )
                            }
                        }
                    }
                )
            } else {
                AppTopAppBar(
                    title = { Text(text = state.selectedUids?.size.toString()) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(ArchiveEvent.OnAllShoppingListsSelected(false)) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.archive_contentDescription_cancelSelectingShoppingLists)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(ArchiveEvent.OnMoveShoppingListSelected(ShoppingLocation.PURCHASES)) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_unarchive),
                                contentDescription = stringResource(R.string.archive_contentDescription_moveShoppingListsToPurchases)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ArchiveEvent.OnMoveShoppingListSelected(ShoppingLocation.TRASH)) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.archive_contentDescription_moveShoppingListsToTrash)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ArchiveEvent.OnAllShoppingListsSelected(true)) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_select_all),
                                contentDescription = stringResource(R.string.shoppingLists_action_selectAllShoppingLists)
                            )
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
                            valueFontSize = state.fontSize.textField.sp,
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
                                fontSize = state.fontSize.button.sp,
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
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.archive_contentDescription_archiveMenuIcon)
                            )
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
                                    right = {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "",
                                            tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                                        )
                                    },
                                    onClick = { viewModel.onEvent(ArchiveEvent.OnSelectDisplayProducts(true)) }
                                )
                                AppDropdownMenuItem(
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_sort)) },
                                    right = {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "",
                                            tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                                        )
                                    },
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
                            AppDropdownMenu(
                                expanded = state.expandedSort,
                                onDismissRequest = { viewModel.onEvent(ArchiveEvent.OnSelectSort(false)) },
                                header = { Text(text = stringResource(id = R.string.shoppingLists_action_sort)) }
                            ) {
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ArchiveEvent.OnSortSelected(SortBy.CREATED)) },
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_sortByCreated)) },
                                    right = {
                                        val checked = state.sortFormatted && state.sortValue.selected.sortBy == SortBy.CREATED
                                        CheckmarkAppCheckbox(checked = checked)
                                    }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ArchiveEvent.OnSortSelected(SortBy.LAST_MODIFIED)) },
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_sortByLastModified)) },
                                    right = {
                                        val checked = state.sortFormatted && state.sortValue.selected.sortBy == SortBy.LAST_MODIFIED
                                        CheckmarkAppCheckbox(checked = checked)
                                    }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ArchiveEvent.OnSortSelected(SortBy.NAME)) },
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_sortByName)) },
                                    right = {
                                        val checked = state.sortFormatted && state.sortValue.selected.sortBy == SortBy.NAME
                                        CheckmarkAppCheckbox(checked = checked)
                                    }
                                )
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ArchiveEvent.OnSortSelected(SortBy.TOTAL)) },
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_sortByTotal)) },
                                    right = {
                                        val checked = state.sortFormatted && state.sortValue.selected.sortBy == SortBy.TOTAL
                                        CheckmarkAppCheckbox(checked = checked)
                                    }
                                )
                                Divider()
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ArchiveEvent.OnReverseSort) },
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_reverseSort)) }
                                )
                                Divider()
                                AppDropdownMenuItem(
                                    onClick = { viewModel.onEvent(ArchiveEvent.OnInvertSortFormatted) },
                                    text = { Text(text = stringResource(R.string.shoppingLists_action_automaticSorting)) },
                                    right = { AppSwitch(checked = state.sortFormatted) }
                                )
                            }
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
            coloredCheckbox = state.coloredCheckbox,
            bottomBar = {
                if (state.displayHiddenShoppingLists) {
                    ShoppingListsHiddenContent(
                        fontSize = state.fontSize,
                        onClick = { viewModel.onEvent(ArchiveEvent.OnShowHiddenShoppingLists(true)) }
                    )
                }
            },
            isWaiting = state.waiting,
            notFound = {
                Text(
                    text = state.notFoundText.asCompose(),
                    fontSize = state.fontSize.itemTitle.sp,
                    textAlign = TextAlign.Center
                )
            },
            isNotFound = state.isNotFound(),
            fontSize = state.fontSize,
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