package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
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
import ru.sokolovromann.myshopping.ui.compose.event.ArchiveScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.UiText
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

    AppScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (screenData.selectedUids == null) {
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
            } else {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(ArchiveEvent.CancelSelectingShoppingLists) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.archive_contentDescription_cancelSelectingShoppingLists),
                                tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(ArchiveEvent.MoveShoppingListsToPurchases) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_unarchive),
                                contentDescription = stringResource(R.string.archive_contentDescription_moveShoppingListsToPurchases),
                                tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(ArchiveEvent.MoveShoppingListsToTrash) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.archive_contentDescription_moveShoppingListsToTrash),
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
                                    viewModel.onEvent(ArchiveEvent.SelectDisplayPurchasesTotal)
                                } else {
                                    viewModel.onEvent(ArchiveEvent.HideDisplayPurchasesTotal)
                                }
                            },
                            onSelected = {
                                val event = ArchiveEvent.DisplayPurchasesTotal(it)
                                viewModel.onEvent(event)
                            }
                        )
                    }
                },
                actionButtons = {
                    IconButton(onClick = { viewModel.onEvent(ArchiveEvent.ShowArchiveMenu) }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.archive_contentDescription_archiveMenuIcon),
                            tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                        )
                        AppDropdownMenu(
                            expanded = screenData.showArchiveMenu,
                            onDismissRequest = { viewModel.onEvent(ArchiveEvent.HideArchiveMenu) }
                        ) {
                            AppDropdownMenuItem(
                                text = { Text(text = stringResource(R.string.shoppingLists_action_sort)) },
                                right = {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "",
                                        tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                                    )
                                },
                                onClick = { viewModel.onEvent(ArchiveEvent.SelectShoppingListsSort) }
                            )
                            AppDropdownMenuItem(
                                text = { Text(text = stringResource(R.string.shoppingLists_action_selectShoppingLists)) },
                                right = {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "",
                                        tint = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
                                    )
                                },
                                onClick = { viewModel.onEvent(ArchiveEvent.SelectSelectShoppingLists) }
                            )
                        }

                        AppDropdownMenu(
                            expanded = screenData.showSort,
                            onDismissRequest = { viewModel.onEvent(ArchiveEvent.HideShoppingListsSort) },
                            header = { Text(text = stringResource(id = R.string.shoppingLists_action_sort)) }
                        ) {
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(ArchiveEvent.SortShoppingLists(SortBy.CREATED)) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_sortByCreated)) }
                            )
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(ArchiveEvent.SortShoppingLists(SortBy.LAST_MODIFIED)) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_sortByLastModified)) }
                            )
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(ArchiveEvent.SortShoppingLists(SortBy.NAME)) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_sortByName)) }
                            )
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(ArchiveEvent.SortShoppingLists(SortBy.TOTAL)) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_sortByTotal)) }
                            )
                        }

                        AppDropdownMenu(
                            expanded = screenData.showSelectingMenu,
                            onDismissRequest = { viewModel.onEvent(ArchiveEvent.HideSelectShoppingLists) },
                            header = { Text(text = stringResource(id = R.string.shoppingLists_action_selectShoppingLists)) }
                        ) {
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(ArchiveEvent.SelectAllShoppingLists) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_selectAllShoppingListsTo)) }
                            )
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(ArchiveEvent.SelectCompletedShoppingLists) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_selectCompletedShoppingListsTo)) }
                            )
                            AppDropdownMenuItem(
                                onClick = { viewModel.onEvent(ArchiveEvent.SelectActiveShoppingLists) },
                                text = { Text(text = stringResource(R.string.shoppingLists_action_selectActiveShoppingListsTo)) }
                            )
                        }
                    }
                }
            )
        },
        drawerContent = {
            AppDrawerContent(
                selected = UiRoute.Archive,
                onItemClick = {
                    val event = ArchiveEvent.SelectNavigationItem(it)
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
                        onClick = { viewModel.onEvent(ArchiveEvent.DisplayHiddenShoppingLists) }
                    )
                }
            },
            notFound = {
                Text(
                    text = stringResource(R.string.archive_text_shoppingListsNotFound),
                    fontSize = screenData.fontSize.toItemTitle().sp,
                    textAlign = TextAlign.Center
                )
            },
            fontSize = screenData.fontSize,
            onClick = {
                val uids = screenData.selectedUids
                val event = if (uids == null) {
                    ArchiveEvent.ShowProducts(it)
                } else {
                    if (uids.contains(it)) {
                        ArchiveEvent.UnselectShoppingList(it)
                    } else {
                        ArchiveEvent.SelectShoppingList(it)
                    }
                }
                viewModel.onEvent(event)
            },
            onLongClick = {
                if (screenData.selectedUids == null) {
                    val event = ArchiveEvent.SelectShoppingList(it)
                    viewModel.onEvent(event)
                }
            },
            selectedUids = screenData.selectedUids
        )
    }
}