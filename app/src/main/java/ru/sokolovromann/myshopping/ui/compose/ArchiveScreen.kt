package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
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
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.ArchiveScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.ListData
import ru.sokolovromann.myshopping.ui.compose.state.ListResult
import ru.sokolovromann.myshopping.ui.compose.state.ShoppingListItem
import ru.sokolovromann.myshopping.ui.compose.state.TextData
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.viewmodel.ArchiveViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.ArchiveEvent

@Composable
fun ArchiveScreen(
    navController: NavController,
    viewModel: ArchiveViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()

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

    AppSystemUi(systemUiController = systemUiController)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(viewModel) },
        bottomBar = { BottomBar(viewModel) },
        drawerContent = { DrawerContent(viewModel) },
        content = { paddingValues -> Content(paddingValues, viewModel) }
    )
}

@Composable
private fun TopBar(viewModel: ArchiveViewModel) {
    TopAppBar(
        title = { Text(text = viewModel.topBarState.value.title.text.asCompose()) },
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
}

@Composable
private fun BottomBar(viewModel: ArchiveViewModel) {
    AppBottomAppBar {
        TextButton(
            onClick = { viewModel.onEvent(ArchiveEvent.SelectShoppingListsDisplayTotal)}
        ) {
            val total = viewModel.totalState.currentData.text
            Text(
                text = total.text.asCompose(),
                fontSize = total.fontSize
            )
            TotalMenu(viewModel)
        }
    }
}

@Composable
private fun DrawerContent(viewModel: ArchiveViewModel) {
    AppDrawerContent(
        selected = UiRoute.Archive,
        onItemClick = {
            val event = ArchiveEvent.SelectNavigationItem(it)
            viewModel.onEvent(event)
        }
    )
}

@Composable
private fun Content(paddingValues: PaddingValues, viewModel: ArchiveViewModel) {
    Box(modifier = Modifier.padding(paddingValues)) {
        val listData = viewModel.archiveState.currentData
        when (listData.result) {
            ListResult.Showing -> ArchiveShowing(listData, viewModel)
            ListResult.NotFound -> ArchiveNotFound(listData.notFoundText)
            ListResult.Loading -> ArchiveLoading()
            ListResult.Nothing -> {}
        }
    }
}

@Composable
private fun ArchiveShowing(data: ListData<ShoppingListItem>, viewModel: ArchiveViewModel) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 4.dp)
        .verticalScroll(scrollState)
    ) {
        ArchiveBar(viewModel)
        AppGrid(multiColumns = data.multiColumns) {
            data.items.forEach { item -> ArchiveItem(item, viewModel)}
        }
        Spacer(modifier = Modifier.height(128.dp))
    }
}

@Composable
private fun ArchiveBar(viewModel: ArchiveViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            TextButton(onClick = { viewModel.onEvent(ArchiveEvent.SelectShoppingListsSort) }) {
                val sortState = viewModel.sortState.currentData.text
                Text(
                    text = sortState.text.asCompose(),
                    fontSize = sortState.fontSize
                )
                SortMenu(viewModel)
            }
            IconButton(onClick = { viewModel.onEvent(ArchiveEvent.InvertShoppingListsSort) }) {
                Icon(
                    painter = viewModel.sortAscendingState.value.icon.asPainter() ?: return@IconButton,
                    contentDescription = stringResource(R.string.shoppingLists_contentDescription_sortAscendingIcon),
                    tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                )
            }
        }
        IconButton(onClick = { viewModel.onEvent(ArchiveEvent.SelectShoppingListsDisplayCompleted) }) {
            Icon(
                painter = viewModel.completedState.currentData.icon.icon.asPainter() ?: return@IconButton,
                contentDescription = stringResource(R.string.shoppingLists_contentDescription_displayCompletedIcon),
                tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
            )
            CompletedMenu(viewModel)
        }
    }
}

@Composable
private fun ArchiveNotFound(data: TextData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = data.text.asCompose(),
            fontSize = data.fontSize,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onBackground
        )
    }
}

@Composable
private fun ArchiveLoading() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        content = { CircularProgressIndicator() }
    )
}

@Composable
private fun ArchiveItem(item: ShoppingListItem, viewModel: ArchiveViewModel) {
    ShoppingListSurfaceItem(
        shoppingListItem = item,
        fontSize = FontSize.MEDIUM,
        dropdownMenu = { ItemMenu(item.uid, viewModel) },
        onClick = {
            val event = ArchiveEvent.ShowProducts(item.uid)
            viewModel.onEvent(event)
        },
        onLongClick = {
            val event = ArchiveEvent.ShowShoppingListMenu(item.uid)
            viewModel.onEvent(event)
        }
    )
}

@Composable
private fun TotalMenu(viewModel: ArchiveViewModel) {
    val totalData = viewModel.totalState.currentData
    val menu = totalData.menu ?: return

    AppDropdownMenu(
        expanded = totalData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(ArchiveEvent.HideShoppingListsDisplayTotal) },
        header = { Text(text = menu.title.text.asCompose()) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.DisplayShoppingListsAllTotal) },
            text = { Text(text = menu.allBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.allSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.DisplayShoppingListsCompletedTotal) },
            text = { Text(text = menu.completedBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.completedSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.DisplayShoppingListsActiveTotal) },
            text = { Text(text = menu.activeBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.activeSelected.selected) }
        )
    }
}

@Composable
private fun SortMenu(viewModel: ArchiveViewModel) {
    val sortData = viewModel.sortState.currentData
    val menu = sortData.menu ?: return

    AppDropdownMenu(
        expanded = sortData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(ArchiveEvent.HideShoppingListsSort) },
        header = { Text(text = menu.title.text.asCompose()) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.SortShoppingListsByCreated) },
            text = { Text(text = menu.byCreatedBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byCreatedSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.SortShoppingListsByLastModified) },
            text = { Text(text = menu.byLastModifiedBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byLastModifiedSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.SortShoppingListsByName) },
            text = { Text(text = menu.byNameBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byNameSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.SortShoppingListsByTotal) },
            text = { Text(text = menu.byTotalBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byTotalSelected.selected) }
        )
    }
}

@Composable
private fun CompletedMenu(viewModel: ArchiveViewModel) {
    val completedData = viewModel.completedState.currentData
    val menu = completedData.menu ?: return

    AppDropdownMenu(
        expanded = completedData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(ArchiveEvent.HideShoppingListsDisplayCompleted) },
        header = { Text(text = menu.title.text.asCompose()) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.DisplayShoppingListsCompletedFirst) },
            text = { Text(text = menu.firstBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.firstSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.DisplayShoppingListsCompletedLast) },
            text = { Text(text = menu.lastBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.lastSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.HideShoppingListsCompleted) },
            text = { Text(text = menu.hideBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.hideSelected.selected) }
        )
    }
}

@Composable
private fun ItemMenu(itemUid: String, viewModel: ArchiveViewModel) {
    val itemMenuData = viewModel.itemMenuState.currentData
    val menu = itemMenuData.menu ?: return

    AppDropdownMenu(
        expanded = itemMenuData.itemUid == itemUid,
        onDismissRequest = { viewModel.onEvent(ArchiveEvent.HideShoppingListMenu) }
    ) {
        AppDropdownMenuItem(
            onClick = {
                val event = ArchiveEvent.MoveShoppingListToPurchases(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = menu.moveToPurchasesBody.text.asCompose()) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = ArchiveEvent.MoveShoppingListToTrash(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = menu.moveToTrashBody.text.asCompose()) }
        )
    }
}