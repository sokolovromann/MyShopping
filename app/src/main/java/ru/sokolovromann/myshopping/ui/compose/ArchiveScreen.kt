package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.ArchiveScreenEvent
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.utils.toButton
import ru.sokolovromann.myshopping.ui.utils.toItemTitle
import ru.sokolovromann.myshopping.ui.viewmodel.ArchiveViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.ArchiveEvent

@Composable
fun ArchiveScreen(
    navController: NavController,
    viewModel: ArchiveViewModel = hiltViewModel()
) {
    val screenData = viewModel.archiveState.screenData
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                ArchiveScreenEvent.ShowBackScreen -> navController.popBackStack()

                is ArchiveScreenEvent.ShowProducts -> navController.navigate(
                    route = UiRoute.Products.productsScreen(it.uid)
                )

                ArchiveScreenEvent.ShowPurchases -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Purchases.purchasesScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                ArchiveScreenEvent.ShowTrash -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Trash.trashScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                ArchiveScreenEvent.ShowAutocompletes -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Autocompletes.autocompletesScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                ArchiveScreenEvent.ShowSettings -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Settings.settingsScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                ArchiveScreenEvent.ShowNavigationDrawer -> coroutineScope.launch {
                    scaffoldState.drawerState.open()
                }

                ArchiveScreenEvent.HideNavigationDrawer -> coroutineScope.launch {
                    scaffoldState.drawerState.close()
                }
            }
        }
    }

    BackHandler(enabled = scaffoldState.drawerState.isOpen) {
        viewModel.onEvent(ArchiveEvent.HideNavigationDrawer)
    }

    AppGridScaffold(
        scaffoldState = scaffoldState,
        screenState = screenData.screenState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.archive_header)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(ArchiveEvent.ShowNavigationDrawer) }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(R.string.archive_contentDescription_navigationIcon),
                            tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (screenData.showBottomBar) {
                AppBottomAppBar {
                    ShoppingListsTotalContent(
                        displayTotal = screenData.displayTotal,
                        totalText = screenData.totalText,
                        fontSize = screenData.fontSize.toButton().sp,
                        expanded = screenData.showDisplayTotal,
                        onExpanded = {
                            if (it) {
                                viewModel.onEvent(ArchiveEvent.SelectShoppingListsDisplayTotal)
                            } else {
                                viewModel.onEvent(ArchiveEvent.HideShoppingListsDisplayTotal)
                            }
                        },
                        onSelected = {
                            val event = ArchiveEvent.DisplayShoppingListsTotal(it)
                            viewModel.onEvent(event)
                        }
                    )
                }
            }
        },
        drawerContent = {
            AppDrawerContent(
                selected = UiRoute.Archive,
                onItemClick = {
                    val event = ArchiveEvent.SelectNavigationItem(it)
                    viewModel.onEvent(event)
                }
            )
        },
        loadingContent = {
            AppLoadingContent(indicator = { CircularProgressIndicator() })
        },
        notFoundContent = {
            AppNotFoundContent {
                Text(
                    text = stringResource(R.string.archive_text_shoppingListsNotFound),
                    fontSize = screenData.fontSize.toItemTitle().sp
                )
            }
        },
        gridBar = {
            ShoppingListsSortContent(
                modifier = Modifier.weight(1f),
                sort = screenData.sort,
                fontSize = screenData.fontSize.toButton().sp,
                expanded = screenData.showSort,
                onExpanded = {
                    if (it) {
                        viewModel.onEvent(ArchiveEvent.SelectShoppingListsSort)
                    } else {
                        viewModel.onEvent(ArchiveEvent.HideShoppingListsSort)
                    }
                },
                onSelected = { viewModel.onEvent(ArchiveEvent.SortShoppingLists(it)) },
                onInverted = { viewModel.onEvent(ArchiveEvent.InvertShoppingListsSort) }
            )
            ShoppingListsDisplayCompletedContent(
                displayCompleted = screenData.displayCompleted,
                expanded = screenData.showDisplayCompleted,
                onExpanded = {
                    if (it) {
                        viewModel.onEvent(ArchiveEvent.SelectShoppingListsDisplayCompleted)
                    } else {
                        viewModel.onEvent(ArchiveEvent.HideShoppingListsDisplayCompleted)
                    }
                },
                onSelected = { viewModel.onEvent(ArchiveEvent.DisplayShoppingListsCompleted(it)) }
            )
        }
    ) {
        ShoppingListsGrid(
            multiColumns = screenData.multiColumns,
            items = screenData.shoppingLists,
            fontSize = screenData.fontSize,
            dropdownMenu = {
                AppDropdownMenu(
                    expanded = it == screenData.shoppingListMenuUid,
                    onDismissRequest = { viewModel.onEvent(ArchiveEvent.HideShoppingListMenu) }
                ) {
                    AppDropdownMenuItem(
                        onClick = {
                            val event = ArchiveEvent.MoveShoppingListToPurchases(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.archive_action_moveShoppingListToPurchases)) }
                    )
                    AppDropdownMenuItem(
                        onClick = {
                            val event = ArchiveEvent.MoveShoppingListToTrash(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.archive_action_moveShoppingListToTrash)) }
                    )
                }
            },
            onClick = {
                val event = ArchiveEvent.ShowProducts(it)
                viewModel.onEvent(event)
            },
            onLongClick = {
                val event = ArchiveEvent.ShowShoppingListMenu(it)
                viewModel.onEvent(event)
            }
        )
    }
}