package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.PurchasesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.viewmodel.PurchasesViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.PurchasesEvent

@ExperimentalFoundationApi
@Composable
fun PurchasesScreen(
    navController: NavController,
    viewModel: PurchasesViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                is PurchasesScreenEvent.ShowProducts -> navController.navigate(
                    route = UiRoute.Products.productsScreen(it.uid)
                )

                PurchasesScreenEvent.ShowArchive -> navController.navigateWithDrawerOption(
                    route = UiRoute.Archive.archiveScreen
                )

                PurchasesScreenEvent.ShowTrash -> navController.navigateWithDrawerOption(
                    route = UiRoute.Trash.trashScreen
                )

                PurchasesScreenEvent.ShowAutocompletes -> navController.navigateWithDrawerOption(
                    route = UiRoute.Autocompletes.autocompletesScreen
                )

                PurchasesScreenEvent.ShowSettings -> navController.navigateWithDrawerOption(
                    route = UiRoute.Settings.settingsScreen
                )

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
    
    AppSystemUi(
        systemUiController = systemUiController,
        data = viewModel.systemUiState.value
    )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(viewModel) },
        bottomBar = { BottomBar(viewModel) },
        drawerContent = { DrawerContent(viewModel) },
        floatingActionButton = { FloatingActionButton(viewModel) },
        content = { paddingValues -> Content(paddingValues, viewModel) }
    )
}

@Composable
private fun TopBar(viewModel: PurchasesViewModel) {
    AppTopBar(
        data = viewModel.topBarState.value,
        onNavigationIconClick = { viewModel.onEvent(PurchasesEvent.ShowNavigationDrawer) }
    )
}

@Composable
private fun BottomBar(viewModel: PurchasesViewModel) {
    AppBottomBar(data = viewModel.bottomBarState.value) {
        TextButton(
            onClick = { viewModel.onEvent(PurchasesEvent.SelectShoppingListsDisplayTotal) }
        ) {
            AppText(data = viewModel.totalState.currentData.text)
            TotalMenu(viewModel)
        }
    }
}

@Composable
private fun DrawerContent(viewModel: PurchasesViewModel) {
    AppNavigationDrawer(
        data = viewModel.navigationDrawerState.value,
        onClick = {
            val event = PurchasesEvent.SelectNavigationItem(it.route)
            viewModel.onEvent(event)
        }
    )
}

@Composable
private fun FloatingActionButton(viewModel: PurchasesViewModel) {
    AppFloatingActionButton(
        modifier = Modifier.offset(y = 42.dp),
        data = viewModel.floatingActionButtonState.value,
        onClick = { viewModel.onEvent(PurchasesEvent.AddShoppingList) }
    )
}

@ExperimentalFoundationApi
@Composable
private fun Content(paddingValues: PaddingValues, viewModel: PurchasesViewModel) {
    Box(modifier = Modifier.padding(paddingValues)) {
        val listData = viewModel.purchasesState.currentData
        when (listData.result) {
            ListResult.Showing -> PurchasesShowing(listData, viewModel)
            ListResult.NotFound -> PurchasesNotFound(listData.notFoundText)
            ListResult.Loading -> PurchasesLoading()
            ListResult.Nothing -> {}
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun PurchasesShowing(data: ListData<ShoppingListItem>, viewModel: PurchasesViewModel) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 4.dp)
        .verticalScroll(scrollState)
    ) {
        PurchasesBar(viewModel)
        AppGrid(data = data) {
            data.items.forEach { item -> PurchasesItem(item, viewModel)}
        }
        Spacer(modifier = Modifier.height(128.dp))
    }
}

@Composable
private fun PurchasesBar(viewModel: PurchasesViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            TextButton(
                onClick = { viewModel.onEvent(PurchasesEvent.SelectShoppingListsSort) }
            ) {
                AppText(data = viewModel.sortState.currentData.text)
                SortMenu(viewModel)
            }
            IconButton(onClick = { viewModel.onEvent(PurchasesEvent.InvertShoppingListsSort) }) {
                AppIcon(data = viewModel.sortAscendingState.value)
            }
        }
        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.SelectShoppingListsDisplayCompleted) }) {
            AppIcon(data = viewModel.completedState.currentData.icon)
            CompletedMenu(viewModel)
        }
    }
}

@Composable
private fun PurchasesNotFound(data: TextData) {
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
private fun PurchasesLoading() {
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
private fun PurchasesItem(item: ShoppingListItem, viewModel: PurchasesViewModel) {
    AppSurfaceItem(
        title = itemTitleOrNull(item),
        body = itemBodyOrNull(item),
        dropdownMenu = { ItemMenu(item.uid, viewModel) },
        onClick = {
            val event = PurchasesEvent.ShowProducts(item.uid)
            viewModel.onEvent(event)
        },
        onLongClick = {
            val event = PurchasesEvent.ShowShoppingListMenu(item.uid)
            viewModel.onEvent(event)
        }
    )
}

@Composable
private fun TotalMenu(viewModel: PurchasesViewModel) {
    val totalData = viewModel.totalState.currentData
    val menu = totalData.menu ?: return

    DropdownMenu(
        expanded = totalData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListsDisplayTotal) }
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
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsAllTotal) }
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.completedSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.completedBody
                )
            },
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsCompletedTotal) }
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.activeSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.activeBody
                )
            },
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsActiveTotal) }
        )
    }
}

@Composable
private fun SortMenu(viewModel: PurchasesViewModel) {
    val sortData = viewModel.sortState.currentData
    val menu = sortData.menu ?: return

    DropdownMenu(
        expanded = sortData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListsSort) }
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
            onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingListsByCreated) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.byLastModifiedSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.byLastModifiedBody
                )
            },
            onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingListsByLastModified) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.byNameSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.byNameBody
                )
            },
            onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingListsByName) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.byTotalSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.byTotalBody
                )
            },
            onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingListsByTotal) },
        )
    }
}

@Composable
private fun CompletedMenu(viewModel: PurchasesViewModel) {
    val completedData = viewModel.completedState.currentData
    val menu = completedData.menu ?: return

    DropdownMenu(
        expanded = completedData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListsDisplayCompleted) }
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
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsCompletedFirst) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.lastSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.lastBody
                )
            },
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsCompletedLast) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.hideSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.hideBody
                )
            },
            onClick = { viewModel.onEvent(PurchasesEvent.HideShoppingListsCompleted) },
        )
    }
}

@Composable
private fun ItemMenu(itemUid: String, viewModel: PurchasesViewModel) {
    val itemMenuData = viewModel.itemMenuState.currentData
    val menu = itemMenuData.menu ?: return

    DropdownMenu(
        expanded = itemMenuData.itemUid == itemUid,
        onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListMenu) }
    ) {
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.moveToArchiveBody
                )
            },
            onClick = {
                val event = PurchasesEvent.MoveShoppingListToArchive(itemUid)
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.moveToTrashBody
                )
            },
            onClick = {
                val event = PurchasesEvent.MoveShoppingListToTrash(itemUid)
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