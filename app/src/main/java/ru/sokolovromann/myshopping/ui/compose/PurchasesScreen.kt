package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.PurchasesScreenEvent
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
            if (screenData.showBottomBar) {
                AppBottomAppBar {
                    ShoppingListsTotalContent(
                        displayTotal = screenData.displayTotal,
                        totalText = screenData.totalText,
                        fontSize = screenData.fontSize.toButton().sp,
                        expanded = screenData.showDisplayTotal,
                        onExpanded = {
                            if (it) {
                                viewModel.onEvent(PurchasesEvent.SelectShoppingListsDisplayTotal)
                            } else {
                                viewModel.onEvent(PurchasesEvent.HideShoppingListsDisplayTotal)
                            }
                        },
                        onSelected = {
                            val event = PurchasesEvent.DisplayShoppingListsTotal(it)
                            viewModel.onEvent(event)
                        }
                    )
                }
            }
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
        floatingActionButton = {
            PurchasesFloatingActionButton(
                isOffset = screenData.showBottomBar,
                onClick = { viewModel.onEvent(PurchasesEvent.AddShoppingList) }
            )
        },
        loadingContent = {
            AppLoadingContent(indicator = { CircularProgressIndicator() })
        },
        notFoundContent = {
            AppNotFoundContent {
                Text(
                    text = stringResource(R.string.purchases_text_shoppingListsNotFound),
                    fontSize = screenData.fontSize.toItemTitle().sp
                )
            }
        }
    ) {
        ShoppingListsGrid(
            multiColumns = screenData.multiColumns,
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
                            val event = PurchasesEvent.MoveShoppingListToUp(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.purchases_action_moveShoppingListToUp)) }
                    )
                    AppDropdownMenuItem(
                        onClick = {
                            val event = PurchasesEvent.MoveShoppingListToDown(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.purchases_action_moveShoppingListToDown)) }
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

@Composable
private fun PurchasesFloatingActionButton(
    isOffset: Boolean,
    onClick: () -> Unit
) {
    val modifier = if (isOffset) {
        Modifier.offset(y = PurchasesFabVerticalOffset)
    } else {
        Modifier
    }
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.purchases_contentDescription_addShoppingListIcon),
            tint = MaterialTheme.colors.onSecondary
        )
    }
}

private val PurchasesFabVerticalOffset = 42.dp