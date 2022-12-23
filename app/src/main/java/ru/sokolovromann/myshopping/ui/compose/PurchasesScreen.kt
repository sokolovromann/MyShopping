package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import ru.sokolovromann.myshopping.ui.compose.event.PurchasesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.viewmodel.PurchasesViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.PurchasesEvent

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
    
    AppSystemUi(systemUiController = systemUiController)

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
    TopAppBar(
        title = { Text(text = viewModel.topBarState.value.title.text.asCompose()) },
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
}

@Composable
private fun BottomBar(viewModel: PurchasesViewModel) {
    AppBottomAppBar {
        TextButton(
            onClick = { viewModel.onEvent(PurchasesEvent.SelectShoppingListsDisplayTotal) }
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
    FloatingActionButton(
        modifier = Modifier.offset(y = 42.dp),
        onClick = { viewModel.onEvent(PurchasesEvent.AddShoppingList) }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.purchases_contentDescription_addShoppingListIcon),
            tint = MaterialTheme.colors.onSecondary
        )
    }
}

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

@Composable
private fun PurchasesShowing(data: ListData<ShoppingListItem>, viewModel: PurchasesViewModel) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 4.dp)
        .verticalScroll(scrollState)
    ) {
        PurchasesBar(viewModel)
        AppGrid(multiColumns = data.multiColumns) {
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

@Composable
private fun PurchasesItem(item: ShoppingListItem, viewModel: PurchasesViewModel) {
    AppSurfaceItem(
        title = itemTitleOrNull(item),
        body = itemBody(item),
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

    AppDropdownMenu(
        expanded = totalData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListsDisplayTotal) },
        header = { Text(text = menu.title.text.asCompose()) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsAllTotal) },
            text = { Text(text = menu.allBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.allSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsCompletedTotal) },
            text = { Text(text = menu.completedBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.completedSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsActiveTotal) },
            text = { Text(text = menu.activeBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.activeSelected.selected) }
        )
    }
}

@Composable
private fun SortMenu(viewModel: PurchasesViewModel) {
    val sortData = viewModel.sortState.currentData
    val menu = sortData.menu ?: return

    AppDropdownMenu(
        expanded = sortData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListsSort) },
        header = { Text(text = menu.title.text.asCompose()) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingListsByCreated) },
            text = { Text(text = menu.byCreatedBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byCreatedSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingListsByLastModified) },
            text = { Text(text = menu.byLastModifiedBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byLastModifiedSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingListsByName) },
            text = { Text(text = menu.byNameBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byNameSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingListsByTotal) },
            text = { Text(text = menu.byTotalBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byTotalSelected.selected) }
        )
    }
}

@Composable
private fun CompletedMenu(viewModel: PurchasesViewModel) {
    val completedData = viewModel.completedState.currentData
    val menu = completedData.menu ?: return

    AppDropdownMenu(
        expanded = completedData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListsDisplayCompleted) },
        header = { Text(text = menu.title.text.asCompose()) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsCompletedFirst) },
            text = { Text(text = menu.firstBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.firstSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsCompletedLast) },
            text = { Text(text = menu.lastBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.lastSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.HideShoppingListsCompleted) },
            text = { Text(text = menu.hideBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.hideSelected.selected) }
        )
    }
}

@Composable
private fun ItemMenu(itemUid: String, viewModel: PurchasesViewModel) {
    val itemMenuData = viewModel.itemMenuState.currentData
    val menu = itemMenuData.menu ?: return

    AppDropdownMenu(
        expanded = itemMenuData.itemUid == itemUid,
        onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListMenu) }
    ) {
        AppDropdownMenuItem(
            onClick = {
                val event = PurchasesEvent.MoveShoppingListToArchive(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = menu.moveToArchiveBody.text.asCompose()) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = PurchasesEvent.MoveShoppingListToTrash(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = menu.moveToTrashBody.text.asCompose()) }
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