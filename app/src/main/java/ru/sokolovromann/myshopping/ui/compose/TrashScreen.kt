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
import androidx.compose.ui.res.painterResource
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
            AppTopAppBarButton(onClick = { viewModel.onEvent(TrashEvent.DeleteShoppingLists) }) {
                Text(text = viewModel.clearState.value.text.asCompose().uppercase())
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
                fontSize = total.fontSize
            )
            TotalMenu(viewModel)
        }
    }
}

@Composable
private fun DrawerContent(viewModel: TrashViewModel) {
    AppDrawerContent(
        selected = UiRoute.Trash,
        onItemClick = {
            val event = TrashEvent.SelectNavigationItem(it)
            viewModel.onEvent(event)
        }
    )
}

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

@Composable
private fun TrashShowing(data: ListData<ShoppingListItem>, viewModel: TrashViewModel) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 4.dp)
        .verticalScroll(scrollState)
    ) {
        TrashBar(viewModel)
        AppGrid(multiColumns = data.multiColumns) {
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
            TextButton(onClick = { viewModel.onEvent(TrashEvent.SelectShoppingListsSort) }) {
                val sortState = viewModel.sortState.currentData.text
                Text(
                    text = sortState.text.asCompose(),
                    fontSize = sortState.fontSize
                )
                SortMenu(viewModel)
            }
            IconButton(onClick = { viewModel.onEvent(TrashEvent.InvertShoppingListsSort) }) {
                Icon(
                    painter = viewModel.sortAscendingState.value.icon.asPainter() ?: return@IconButton,
                    contentDescription = stringResource(R.string.shoppingLists_contentDescription_sortAscendingIcon),
                    tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                )
            }
        }
        IconButton(onClick = { viewModel.onEvent(TrashEvent.SelectShoppingListsDisplayCompleted) }) {
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

@Composable
private fun TrashItem(item: ShoppingListItem, viewModel: TrashViewModel) {
    AppSurfaceItem(
        title = itemTitleOrNull(item),
        body = itemBody(item),
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

    AppDropdownMenu(
        expanded = totalData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(TrashEvent.HideShoppingListsDisplayTotal) },
        header = { Text(text = menu.title.text.asCompose()) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsAllTotal) },
            text = { Text(text = menu.allBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.allSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsCompletedTotal) },
            text = { Text(text = menu.completedBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.completedSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsActiveTotal) },
            text = { Text(text = menu.activeBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.activeSelected.selected) }
        )
    }
}

@Composable
private fun SortMenu(viewModel: TrashViewModel) {
    val sortData = viewModel.sortState.currentData
    val menu = sortData.menu ?: return

    AppDropdownMenu(
        expanded = sortData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(TrashEvent.HideShoppingListsSort) },
        header = { Text(text = menu.title.text.asCompose()) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.SortShoppingListsByCreated) },
            text = { Text(text = menu.byCreatedBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byCreatedSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.SortShoppingListsByLastModified) },
            text = { Text(text = menu.byLastModifiedBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byLastModifiedSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.SortShoppingListsByName) },
            text = { Text(text = menu.byNameBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byNameSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.SortShoppingListsByTotal) },
            text = { Text(text = menu.byTotalBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byTotalSelected.selected) }
        )
    }
}

@Composable
private fun CompletedMenu(viewModel: TrashViewModel) {
    val completedData = viewModel.completedState.currentData
    val menu = completedData.menu ?: return

    AppDropdownMenu(
        expanded = completedData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(TrashEvent.HideShoppingListsDisplayCompleted) },
        header = { Text(text = menu.title.text.asCompose()) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsCompletedFirst) },
            text = { Text(text = menu.firstBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.firstSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsCompletedLast) },
            text = { Text(text = menu.lastBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.lastSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.HideShoppingListsCompleted) },
            text = { Text(text = menu.hideBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.hideSelected.selected) }
        )
    }
}

@Composable
private fun ItemMenu(itemUid: String, viewModel: TrashViewModel) {
    val itemMenuData = viewModel.itemMenuState.currentData
    val menu = itemMenuData.menu ?: return

    AppDropdownMenu(
        expanded = itemMenuData.itemUid == itemUid,
        onDismissRequest = { viewModel.onEvent(TrashEvent.HideShoppingListMenu) }
    ) {
        AppDropdownMenuItem(
            onClick = {
                val event = TrashEvent.MoveShoppingListToPurchases(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = menu.moveToPurchasesBody.text.asCompose()) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = TrashEvent.MoveShoppingListToArchive(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = menu.moveToArchiveBody.text.asCompose()) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = TrashEvent.DeleteShoppingList(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = menu.deleteBody.text.asCompose()) }
        )
    }
}

@Composable
private fun itemTitleOrNull(item: ShoppingListItem): @Composable (() -> Unit)? {
    val title = item.title
    return itemOrNull(enabled = title.isTextShowing()) {
        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text = title.text.asCompose(),
            fontSize = title.fontSize
        )
    }
}

@Composable
private fun itemBody(item: ShoppingListItem): @Composable (() -> Unit) = {
    Column {
        item.productsBody.forEach {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                val painter = it.first.icon.asPainter() ?: painterResource(R.drawable.ic_all_check_box_outline)
                Icon(
                    modifier = Modifier.size(it.first.size),
                    painter = painter,
                    contentDescription = "",
                    tint = contentColorFor(MaterialTheme.colors.onSurface).copy(ContentAlpha.medium)
                )

                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = it.second.text.asCompose(),
                    fontSize = it.second.fontSize
                )
            }
        }

        val totalBody = item.totalBody
        if (totalBody.isTextShowing()) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = totalBody.text.asCompose(),
                fontSize = totalBody.fontSize
            )
        }

        val reminderBody = item.reminderBody
        if (reminderBody.isTextShowing()) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = reminderBody.text.asCompose(),
                fontSize = reminderBody.fontSize
            )
        }
    }
}