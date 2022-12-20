package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.TrashScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.ListData
import ru.sokolovromann.myshopping.ui.compose.state.ListResult
import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListItem
import ru.sokolovromann.myshopping.ui.compose.state.TextData
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.viewmodel.TrashViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.TrashEvent

@ExperimentalFoundationApi
@Composable
fun TrashScreen(
    navController: NavController,
    viewModel: TrashViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                TrashScreenEvent.ShowBackScreen -> navController.popBackStack()

                is TrashScreenEvent.ShowProducts -> navController.navigate(
                    route = UiRoute.Products.productsScreen(it.uid)
                )

                TrashScreenEvent.ShowPurchases -> navController.navigateWithDrawerOption(
                    route = UiRoute.Purchases.purchasesScreen
                )

                TrashScreenEvent.ShowArchive -> navController.navigateWithDrawerOption(
                    route = UiRoute.Archive.archiveScreen
                )

                TrashScreenEvent.ShowAutocompletes -> navController.navigateWithDrawerOption(
                    route = UiRoute.Autocompletes.autocompletesScreen
                )

                TrashScreenEvent.ShowSettings -> navController.navigateWithDrawerOption(
                    route = UiRoute.Settings.settingsScreen
                )

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

    AppSystemUi(
        systemUiController = systemUiController,
        data = viewModel.systemUiState.value
    )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(viewModel) },
        bottomBar = { BottomBar(viewModel) },
        drawerContent = { DrawerContent(viewModel) },
        content = { paddingValues -> Content(paddingValues, viewModel) }
    )
}

@Composable
private fun TopBar(viewModel: TrashViewModel) {
    TopAppBar(
        title = { Text(text = viewModel.topBarState.value.title.text.asCompose()) },
        navigationIcon = {
            IconButton(onClick = { viewModel.onEvent(TrashEvent.ShowNavigationDrawer) }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.trash_contentDescription_navigationIcon),
                    tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                )
            }
        },
        actions = {
            TextButton(onClick = { viewModel.onEvent(TrashEvent.DeleteShoppingLists) }) {
                AppText(data = viewModel.clearState.value)
            }
        }
    )
}

@Composable
private fun BottomBar(viewModel: TrashViewModel) {
    AppBottomAppBar {
        TextButton(
            onClick = { viewModel.onEvent(TrashEvent.SelectShoppingListsDisplayTotal)}
        ) {
            val total = viewModel.totalState.currentData.text
            Text(
                text = total.text.asCompose(),
                fontSize = total.fontSize,
                color = MaterialTheme.colors.onBackground,
                style = total.style
            )
            TotalMenu(viewModel)
        }
    }
}

@Composable
private fun DrawerContent(viewModel: TrashViewModel) {
    AppNavigationDrawer(
        data = viewModel.navigationDrawerState.value,
        onClick = {
            val event = TrashEvent.SelectNavigationItem(it.route)
            viewModel.onEvent(event)
        }
    )
}

@ExperimentalFoundationApi
@Composable
private fun Content(paddingValues: PaddingValues, viewModel: TrashViewModel) {
    Box(modifier = Modifier.padding(paddingValues)) {
        val listData = viewModel.trashState.currentData
        when (listData.result) {
            ListResult.Showing -> TrashShowing(listData, viewModel)
            ListResult.NotFound -> TrashNotFound(listData.notFoundText)
            ListResult.Loading -> TrashLoading()
            ListResult.Nothing -> {}
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun TrashShowing(data: ListData<ShoppingListItem>, viewModel: TrashViewModel) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 4.dp)
        .verticalScroll(scrollState)
    ) {
        TrashBar(viewModel)
        AppGrid(data = data) {
            data.items.forEach { item -> TrashItem(item, viewModel)}
        }
        Spacer(modifier = Modifier.height(128.dp))
    }
}

@Composable
private fun TrashBar(viewModel: TrashViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            TextButton(
                onClick = { viewModel.onEvent(TrashEvent.SelectShoppingListsSort) }
            ) {
                AppText(data = viewModel.sortState.currentData.text)
                SortMenu(viewModel)
            }
            IconButton(onClick = { viewModel.onEvent(TrashEvent.InvertShoppingListsSort) }) {
                AppIcon(data = viewModel.sortAscendingState.value)
            }
        }
        IconButton(onClick = { viewModel.onEvent(TrashEvent.SelectShoppingListsDisplayCompleted) }) {
            AppIcon(data = viewModel.completedState.currentData.icon)
            CompletedMenu(viewModel)
        }
    }
}

@Composable
private fun TrashNotFound(data: TextData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        content = { AppText(data = data) }
    )
}

@Composable
private fun TrashLoading() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        content = { CircularProgressIndicator() }
    )
}

@ExperimentalFoundationApi
@Composable
private fun TrashItem(item: ShoppingListItem, viewModel: TrashViewModel) {
    AppSurfaceItem(
        title = itemTitleOrNull(item),
        body = itemBodyOrNull(item),
        dropdownMenu = { ItemMenu(item.uid, viewModel) },
        onClick = {
            val event = TrashEvent.ShowProducts(item.uid)
            viewModel.onEvent(event)
        },
        onLongClick = {
            val event = TrashEvent.ShowShoppingListMenu(item.uid)
            viewModel.onEvent(event)
        }
    )
}

@Composable
private fun TotalMenu(viewModel: TrashViewModel) {
    val totalData = viewModel.totalState.currentData
    val menu = totalData.menu ?: return

    DropdownMenu(
        expanded = totalData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(TrashEvent.HideShoppingListsDisplayTotal) }
    ) {
        AppText(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            data = menu.title
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.allSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.allBody
                )
            },
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsAllTotal) }
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.completedSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.completedBody
                )
            },
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsCompletedTotal) }
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.activeSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.activeBody
                )
            },
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsActiveTotal) }
        )
    }
}

@Composable
private fun SortMenu(viewModel: TrashViewModel) {
    val sortData = viewModel.sortState.currentData
    val menu = sortData.menu ?: return

    DropdownMenu(
        expanded = sortData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(TrashEvent.HideShoppingListsSort) }
    ) {
        AppText(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            data = menu.title
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.byCreatedSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.byCreatedBody
                )
            },
            onClick = { viewModel.onEvent(TrashEvent.SortShoppingListsByCreated) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.byLastModifiedSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.byLastModifiedBody
                )
            },
            onClick = { viewModel.onEvent(TrashEvent.SortShoppingListsByLastModified) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.byNameSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.byNameBody
                )
            },
            onClick = { viewModel.onEvent(TrashEvent.SortShoppingListsByName) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.byTotalSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.byTotalBody
                )
            },
            onClick = { viewModel.onEvent(TrashEvent.SortShoppingListsByTotal) },
        )
    }
}

@Composable
private fun CompletedMenu(viewModel: TrashViewModel) {
    val completedData = viewModel.completedState.currentData
    val menu = completedData.menu ?: return

    DropdownMenu(
        expanded = completedData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(TrashEvent.HideShoppingListsDisplayCompleted) }
    ) {
        AppText(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            data = menu.title
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.firstSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.firstBody
                )
            },
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsCompletedFirst) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.lastSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.lastBody
                )
            },
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsCompletedLast) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.hideSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.hideBody
                )
            },
            onClick = { viewModel.onEvent(TrashEvent.HideShoppingListsCompleted) },
        )
    }
}

@Composable
private fun ItemMenu(itemUid: String, viewModel: TrashViewModel) {
    val itemMenuData = viewModel.itemMenuState.currentData
    val menu = itemMenuData.menu ?: return

    DropdownMenu(
        expanded = itemMenuData.itemUid == itemUid,
        onDismissRequest = { viewModel.onEvent(TrashEvent.HideShoppingListMenu) }
    ) {
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.moveToPurchasesBody
                )
            },
            onClick = {
                val event = TrashEvent.MoveShoppingListToPurchases(itemUid)
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.moveToArchiveBody
                )
            },
            onClick = {
                val event = TrashEvent.MoveShoppingListToArchive(itemUid)
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.deleteBody
                )
            },
            onClick = {
                val event = TrashEvent.DeleteShoppingList(itemUid)
                viewModel.onEvent(event)
            }
        )
    }
}

@Composable
private fun itemTitleOrNull(item: ShoppingListItem): @Composable (() -> Unit)? {
    if (item.title.isTextHiding()) {
        return null
    }

    return {
        AppText(data = item.title)
        Spacer(modifier = Modifier.padding(4.dp))
    }
}

@Composable
private fun itemBodyOrNull(item: ShoppingListItem): @Composable (() -> Unit)? {
    return {
        Column {
            item.productsBody.forEach {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    AppIcon(data = it.first)
                    Spacer(modifier = Modifier.size(4.dp))
                    AppText(data = it.second)
                }
            }

            if (item.totalBody.isTextShowing()) {
                AppText(
                    modifier = Modifier.padding(top = 8.dp),
                    data = item.totalBody
                )
            }

            if (item.reminderBody.isTextShowing()) {
                AppText(
                    modifier = Modifier.padding(top = 8.dp),
                    data = item.reminderBody
                )
            }
        }
    }
}