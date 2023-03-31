package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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

    AppGridScaffold(
        scaffoldState = scaffoldState,
        screenState = screenData.screenState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.trash_header)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(TrashEvent.ShowNavigationDrawer) }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(R.string.trash_contentDescription_navigationIcon),
                            tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                        )
                    }
                }
            )
        },
        drawerContent = {
            AppDrawerContent(
                selected = UiRoute.Trash,
                onItemClick = {
                    val event = TrashEvent.SelectNavigationItem(it)
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
                    text = stringResource(R.string.trash_text_shoppingListsNotFound),
                    fontSize = screenData.fontSize.toItemTitle().sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        gridBar = {
            TextButton(
                modifier = Modifier.padding(TrashGridBarPaddings),
                onClick = { viewModel.onEvent(TrashEvent.DeleteShoppingLists) }
            ) {
                Text(
                    text = stringResource(R.string.trash_action_deleteShoppingLists),
                    fontSize = screenData.fontSize.toButton().sp
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
                    onDismissRequest = { viewModel.onEvent(TrashEvent.HideShoppingListMenu) }
                ) {
                    AppDropdownMenuItem(
                        onClick = {
                            val event = TrashEvent.MoveShoppingListToPurchases(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.trash_action_moveShoppingListToPurchases)) }
                    )
                    AppDropdownMenuItem(
                        onClick = {
                            val event = TrashEvent.MoveShoppingListToArchive(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.trash_action_moveShoppingListToArchive)) }
                    )
                    AppDropdownMenuItem(
                        onClick = {
                            val event = TrashEvent.DeleteShoppingList(it)
                            viewModel.onEvent(event)
                        },
                        text = { Text(text = stringResource(R.string.trash_action_deleteShoppingList)) }
                    )
                }
            },
            onClick = {
                val event = TrashEvent.ShowProducts(it)
                viewModel.onEvent(event)
            },
            onLongClick = {
                val event = TrashEvent.ShowShoppingListMenu(it)
                viewModel.onEvent(event)
            }
        )
    }
}

private val TrashGridBarPaddings = PaddingValues(horizontal = 8.dp)