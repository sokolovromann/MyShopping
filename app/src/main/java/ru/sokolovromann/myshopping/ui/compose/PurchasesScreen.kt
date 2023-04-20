package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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

    AppScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (screenData.selectedUids == null) {
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
            } else {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.CancelSelectingShoppingLists) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.purchases_contentDescription_cancelSelectingShoppingLists),
                                tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.MoveShoppingListsToArchive) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_archive),
                                contentDescription = stringResource(R.string.purchases_contentDescription_moveShoppingListsToArchive),
                                tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.MoveShoppingListsToTrash) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.purchases_contentDescription_moveShoppingListsToTrash),
                                tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                            )
                        }
                    }
                )
            }
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
                                text = { Text(text = stringResource(R.string.shoppingLists_action_selectShoppingLists)) },
                                after = {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "",
                                        tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                                    )
                                },
                                onClick = { viewModel.onEvent(PurchasesEvent.SelectSelectShoppingLists) }
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
                            expanded = screenData.showSelectingMenu,
                            onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideSelectShoppingLists) },
                            header = { Text(text = stringResource(id = R.string.shoppingLists_action_selectShoppingLists)) }
                        ) {
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(PurchasesEvent.SelectAllShoppingLists) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_selectAllShoppingListsTo)) }
                            )
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(PurchasesEvent.SelectCompletedShoppingLists) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_selectCompletedShoppingListsTo)) }
                            )
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(PurchasesEvent.SelectActiveShoppingLists) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_selectActiveShoppingListsTo)) }
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
        }
    ) { paddings ->
        ShoppingListsGrid(
            modifier = Modifier.padding(paddings),
            screenState = screenData.screenState,
            multiColumns = screenData.multiColumns,
            smartphoneScreen = screenData.smartphoneScreen,
            items = screenData.shoppingLists,
            bottomBar = {
                if (screenData.showHiddenShoppingLists) {
                    ShoppingListsHiddenContent(
                        fontSize = screenData.fontSize,
                        onClick = { viewModel.onEvent(PurchasesEvent.DisplayHiddenShoppingLists) }
                    )
                }
            },
            notFound = {
                Text(
                    text = stringResource(R.string.purchases_text_shoppingListsNotFound),
                    fontSize = screenData.fontSize.toItemTitle().sp,
                    textAlign = TextAlign.Center
                )
            },
            fontSize = screenData.fontSize,
            onClick = {
                val uids = screenData.selectedUids
                val event = if (uids == null) {
                    PurchasesEvent.ShowProducts(it)
                } else {
                    if (uids.contains(it)) {
                        PurchasesEvent.UnselectShoppingList(it)
                    } else {
                        PurchasesEvent.SelectShoppingList(it)
                    }
                }
                viewModel.onEvent(event)
            },
            onLongClick = {
                if (screenData.selectedUids == null) {
                    val event = PurchasesEvent.SelectShoppingList(it)
                    viewModel.onEvent(event)
                }
            },
            selectedUids = screenData.selectedUids
        )
    }
}