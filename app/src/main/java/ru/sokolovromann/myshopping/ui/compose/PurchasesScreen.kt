package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.PurchasesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.UiText
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.utils.toButton
import ru.sokolovromann.myshopping.ui.utils.toItemTitle
import ru.sokolovromann.myshopping.ui.viewmodel.PurchasesViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.PurchasesEvent

@Composable
fun PurchasesScreen(
    navController: NavController,
    viewModel: PurchasesViewModel = hiltViewModel()
) {
    val screenData = viewModel.purchasesState.screenData
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                is PurchasesScreenEvent.ShowProducts -> navController.navigate(
                    route = UiRoute.Products.productsScreen(it.uid)
                )

                PurchasesScreenEvent.ShowArchive -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Archive.archiveScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                PurchasesScreenEvent.ShowTrash -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Trash.trashScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                PurchasesScreenEvent.ShowAutocompletes -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Autocompletes.autocompletesScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                PurchasesScreenEvent.ShowSettings -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Settings.settingsScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                PurchasesScreenEvent.ShowNavigationDrawer -> coroutineScope.launch {
                    scaffoldState.drawerState.open()
                }

                PurchasesScreenEvent.HideNavigationDrawer -> coroutineScope.launch {
                    scaffoldState.drawerState.close()
                }

                PurchasesScreenEvent.FinishApp -> navController.popBackStack()
            }
        }
    }

    BackHandler(enabled = scaffoldState.drawerState.isOpen) {
        viewModel.onEvent(PurchasesEvent.HideNavigationDrawer)
    }

    AppGridScaffold(
        scaffoldState = scaffoldState,
        screenState = screenData.screenState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.purchases_header)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(PurchasesEvent.ShowNavigationDrawer) }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(R.string.purchases_contentDescription_navigationIcon),
                            tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                        )
                    }
                }
            )
        },
        bottomBar = {
            AppBottomAppBar(
                content = {
                    if (screenData.totalText != UiText.Nothing) {
                        ShoppingListsTotalContent(
                            displayTotal = screenData.displayTotal,
                            totalText = screenData.totalText,
                            fontSize = screenData.fontSize.toButton().sp,
                            expanded = screenData.showDisplayTotal,
                            onExpanded = {
                                if (it) {
                                    viewModel.onEvent(PurchasesEvent.SelectDisplayPurchasesTotal)
                                } else {
                                    viewModel.onEvent(PurchasesEvent.HideDisplayPurchasesTotal)
                                }
                            },
                            onSelected = {
                                val event = PurchasesEvent.DisplayPurchasesTotal(it)
                                viewModel.onEvent(event)
                            }
                        )
                    }
                },
                actionButtons = {
                    IconButton(onClick = { viewModel.onEvent(PurchasesEvent.AddShoppingList) }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.purchases_contentDescription_addShoppingListIcon),
                            tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                        )
                    }
                    IconButton(onClick = { viewModel.onEvent(PurchasesEvent.ShowPurchasesMenu) }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.purchases_contentDescription_purchasesMenuIcon),
                            tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                        )
                        AppDropdownMenu(
                            expanded = screenData.showPurchasesMenu,
                            onDismissRequest = { viewModel.onEvent(PurchasesEvent.HidePurchasesMenu) }
                        ) {
                            AppDropdownMenuItem(
                                text = { Text(text = stringResource(R.string.shoppingLists_action_sort)) },
                                after = {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "",
                                        tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                                    )
                                },
                                onClick = { viewModel.onEvent(PurchasesEvent.SelectShoppingListsSort) }
                            )
                            AppDropdownMenuItem(
                                text = { Text(text = stringResource(R.string.purchases_action_moveShoppingListsToArchive)) },
                                after = {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "",
                                        tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                                    )
                                },
                                onClick = { viewModel.onEvent(PurchasesEvent.SelectShoppingListsToArchive) }
                            )
                            AppDropdownMenuItem(
                                text = { Text(text = stringResource(R.string.purchases_action_moveShoppingListsToTrash)) },
                                after = {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "",
                                        tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                                    )
                                },
                                onClick = { viewModel.onEvent(PurchasesEvent.SelectShoppingListsToDelete) }
                            )
                        }

                        AppDropdownMenu(
                            expanded = screenData.showSort,
                            onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListsSort) },
                            header = { Text(text = stringResource(id = R.string.shoppingLists_action_sort)) }
                        ) {
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingLists(SortBy.CREATED)) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_sortByCreated)) }
                            )
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingLists(SortBy.LAST_MODIFIED)) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_sortByLastModified)) }
                            )
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingLists(SortBy.NAME)) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_sortByName)) }
                            )
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingLists(SortBy.TOTAL)) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_sortByTotal)) }
                            )
                        }

                        AppDropdownMenu(
                            expanded = screenData.showToArchive,
                            onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListsToArchive) },
                            header = { Text(text = stringResource(id = R.string.purchases_action_moveShoppingListsToArchive)) }
                        ) {
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(PurchasesEvent.MoveAllShoppingListsTo(true)) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_moveAllShoppingListsTo)) }
                            )
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(PurchasesEvent.MoveCompletedShoppingListsTo(true)) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_moveCompletedShoppingListsTo)) }
                            )
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(PurchasesEvent.MoveActiveShoppingListsTo(true)) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_moveActiveShoppingListsTo)) }
                            )
                        }

                        AppDropdownMenu(
                            expanded = screenData.showToDelete,
                            onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListsToDelete) },
                            header = { Text(text = stringResource(id = R.string.purchases_action_moveShoppingListsToTrash)) }
                        ) {
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(PurchasesEvent.MoveAllShoppingListsTo(false)) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_moveAllShoppingListsTo)) }
                            )
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(PurchasesEvent.MoveCompletedShoppingListsTo(false)) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_moveCompletedShoppingListsTo)) }
                            )
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(PurchasesEvent.MoveActiveShoppingListsTo(false)) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_moveActiveShoppingListsTo)) }
                            )
                        }
                    }
                }
            )
        },
        drawerContent = {
            AppDrawerContent(
                selected = UiRoute.Purchases,
                onItemClick = {
                    val event = PurchasesEvent.SelectNavigationItem(it)
                    viewModel.onEvent(event)
                }
            )
        },
        gridBottomBar = {
            if (screenData.showHiddenShoppingLists) {
                ShoppingListsHidden(
                    fontSize = screenData.fontSize,
                    onClick = { viewModel.onEvent(PurchasesEvent.DisplayHiddenShoppingLists) }
                )
            }
        },
        loadingContent = {
            AppLoadingContent(indicator = { CircularProgressIndicator() })
        },
        notFoundContent = {
            AppNotFoundContent {
                Text(
                    text = stringResource(R.string.purchases_text_shoppingListsNotFound),
                    fontSize = screenData.fontSize.toItemTitle().sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) {
        ShoppingListsGrid(
            multiColumns = screenData.multiColumns,
            smartphoneScreen = screenData.smartphoneScreen,
            items = screenData.shoppingLists,
            fontSize = screenData.fontSize,
            dropdownMenu = {
                AppDropdownMenu(
                    expanded = it == screenData.shoppingListMenuUid,
                    onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListMenu) }
                ) {
                    AppDropdownMenuItem(
                        onClick = {
                            val event = PurchasesEvent.MoveShoppingListToArchive(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.purchases_action_moveShoppingListToArchive)) }
                    )
                    AppDropdownMenuItem(
                        onClick = {
                            val event = PurchasesEvent.MoveShoppingListToTrash(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.purchases_action_moveShoppingListToTrash)) }
                    )
                    Divider()
                    AppDropdownMenuItem(
                        onClick = {
                            val event = PurchasesEvent.MoveShoppingListUp(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.purchases_action_moveShoppingListUp)) }
                    )
                    AppDropdownMenuItem(
                        onClick = {
                            val event = PurchasesEvent.MoveShoppingListDown(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.purchases_action_moveShoppingListDown)) }
                    )
                }
            },
            onClick = {
                val event = PurchasesEvent.ShowProducts(it)
                viewModel.onEvent(event)
            },
            onLongClick = {
                val event = PurchasesEvent.ShowShoppingListMenu(it)
                viewModel.onEvent(event)
            }
        )
    }
}