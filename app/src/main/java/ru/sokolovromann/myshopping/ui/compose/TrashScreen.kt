package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.TrashScreenEvent
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.utils.toButton
import ru.sokolovromann.myshopping.ui.utils.toItemTitle
import ru.sokolovromann.myshopping.ui.viewmodel.TrashViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.TrashEvent

@Composable
fun TrashScreen(
    navController: NavController,
    viewModel: TrashViewModel = hiltViewModel()
) {
    val screenData = viewModel.trashState.screenData
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                TrashScreenEvent.ShowBackScreen -> navController.popBackStack()

                is TrashScreenEvent.ShowProducts -> navController.navigate(
                    route = UiRoute.Products.productsScreen(it.uid)
                )

                TrashScreenEvent.ShowPurchases -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Purchases.purchasesScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                TrashScreenEvent.ShowArchive -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Archive.archiveScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                TrashScreenEvent.ShowAutocompletes -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Autocompletes.autocompletesScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                TrashScreenEvent.ShowSettings -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Settings.settingsScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                TrashScreenEvent.ShowNavigationDrawer -> coroutineScope.launch {
                    scaffoldState.drawerState.open()
                }

                TrashScreenEvent.HideNavigationDrawer -> coroutineScope.launch {
                    scaffoldState.drawerState.close()
                }
            }
        }
    }

    BackHandler(enabled = scaffoldState.drawerState.isOpen) {
        viewModel.onEvent(TrashEvent.HideNavigationDrawer)
    }

    BackHandler(enabled = screenData.selectedUids != null) {
        viewModel.onEvent(TrashEvent.CancelSelectingShoppingLists)
    }

    AppScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (screenData.selectedUids == null) {
                AppTopAppBar(
                    title = { Text(text = stringResource(R.string.trash_header)) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(TrashEvent.ShowNavigationDrawer) }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.trash_contentDescription_navigationIcon)
                            )
                        }
                    }
                )
            } else {
                AppTopAppBar(
                    title = { Text(text = screenData.selectedUids.size.toString()) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(TrashEvent.CancelSelectingShoppingLists) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.trash_contentDescription_cancelSelectingShoppingLists)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(TrashEvent.MoveShoppingListsToPurchases) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_restore),
                                contentDescription = stringResource(R.string.trash_contentDescription_moveShoppingListsToPurchases)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(TrashEvent.MoveShoppingListsToArchive) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_archive),
                                contentDescription = stringResource(R.string.trash_contentDescription_moveShoppingListsToArchive)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(TrashEvent.DeleteShoppingLists) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.trash_contentDescription_deleteShoppingLists)
                            )
                        }
                        IconButton(onClick = { viewModel.onEvent(TrashEvent.SelectAllShoppingLists) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_select_all),
                                contentDescription = stringResource(R.string.shoppingLists_contentDescription_selectAllShoppingLists)
                            )
                        }
                    }
                )
            }
        },
        drawerContent = {
            AppDrawerContent(
                selected = UiRoute.Trash,
                onItemClick = {
                    val event = TrashEvent.SelectNavigationItem(it)
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
            otherItems = screenData.shoppingLists,
            displayProducts = screenData.displayProducts,
            displayCompleted = screenData.displayCompleted,
            coloredCheckbox = screenData.coloredCheckbox,
            topBar = {
                TextButton(
                    modifier = Modifier.padding(TrashGridBarPaddings),
                    onClick = { viewModel.onEvent(TrashEvent.EmptyTrash) }
                ) {
                    Text(
                        text = stringResource(R.string.trash_action_deleteShoppingLists),
                        fontSize = screenData.fontSize.toButton().sp
                    )
                }
            },
            notFound = {
                Text(
                    text = stringResource(R.string.trash_text_shoppingListsNotFound),
                    fontSize = screenData.fontSize.toItemTitle().sp,
                    textAlign = TextAlign.Center
                )
            },
            fontSize = screenData.fontSize,
            onClick = {
                val uids = screenData.selectedUids
                val event = if (uids == null) {
                    TrashEvent.ShowProducts(it)
                } else {
                    if (uids.contains(it)) {
                        TrashEvent.UnselectShoppingList(it)
                    } else {
                        TrashEvent.SelectShoppingList(it)
                    }
                }
                viewModel.onEvent(event)
            },
            onLongClick = {
                if (screenData.selectedUids == null) {
                    val event = TrashEvent.SelectShoppingList(it)
                    viewModel.onEvent(event)
                }
            },
            selectedUids = screenData.selectedUids
        )
    }
}

private val TrashGridBarPaddings = PaddingValues(horizontal = 8.dp)