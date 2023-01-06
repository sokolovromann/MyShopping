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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayTotal
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.PurchasesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.utils.getAscendingIcon
import ru.sokolovromann.myshopping.ui.utils.getShoppingListsText
import ru.sokolovromann.myshopping.ui.utils.toButton
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
    val screenData = viewModel.purchasesState.screenData
    if (!screenData.showBottomBar) {
        return
    }

    AppBottomAppBar {
        TextButton(
            onClick = { viewModel.onEvent(PurchasesEvent.SelectShoppingListsDisplayTotal) }
        ) {
            Text(
                text = screenData.totalText.asCompose(),
                fontSize = screenData.fontSize.toButton().sp
            )
            TotalMenu(viewModel)
        }
    }
}

@Composable
private fun DrawerContent(viewModel: PurchasesViewModel) {
    AppDrawerContent(
        selected = UiRoute.Purchases,
        onItemClick = {
            val event = PurchasesEvent.SelectNavigationItem(it)
            viewModel.onEvent(event)
        }
    )
}

@Composable
private fun FloatingActionButton(viewModel: PurchasesViewModel) {
    val modifier = if (viewModel.purchasesState.screenData.showBottomBar) {
        Modifier.offset(y = 42.dp)
    } else {
        Modifier
    }
    FloatingActionButton(
        modifier = modifier,
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
        when (viewModel.purchasesState.screenData.screenState) {
            ScreenState.Loading -> PurchasesLoading()
            ScreenState.Showing -> PurchasesShowing(viewModel)
            else -> {}
        }
    }
}

@Composable
private fun PurchasesShowing(viewModel: PurchasesViewModel) {
    val scrollState = rememberScrollState()
    val screenData = viewModel.purchasesState.screenData
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 4.dp)
        .verticalScroll(scrollState)
    ) {
        PurchasesBar(viewModel)
        AppGrid(multiColumns = screenData.multiColumns) {
            screenData.shoppingLists.forEach { item -> PurchasesItem(item, viewModel) }
        }
        Spacer(modifier = Modifier.height(128.dp))
    }
}

@Composable
private fun PurchasesBar(viewModel: PurchasesViewModel) {
    val screenData = viewModel.purchasesState.screenData
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            TextButton(onClick = { viewModel.onEvent(PurchasesEvent.SelectShoppingListsSort) }) {
                Text(
                    text = screenData.sort.getShoppingListsText().asCompose(),
                    fontSize = screenData.fontSize.toButton().sp
                )
                SortMenu(viewModel)
            }
            IconButton(onClick = { viewModel.onEvent(PurchasesEvent.InvertShoppingListsSort) }) {
                Icon(
                    painter = screenData.sort.getAscendingIcon().asPainter() ?: return@IconButton,
                    contentDescription = stringResource(R.string.shoppingLists_contentDescription_sortAscendingIcon),
                    tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                )
            }
        }
        IconButton(onClick = { viewModel.onEvent(PurchasesEvent.SelectShoppingListsDisplayCompleted) }) {
            Icon(
                painter = painterResource(R.drawable.ic_all_display_completed),
                contentDescription = stringResource(R.string.shoppingLists_contentDescription_displayCompletedIcon),
                tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
            )
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
    ShoppingListSurfaceItem(
        shoppingListItem = item,
        fontSize = FontSize.MEDIUM,
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
    val screenData = viewModel.purchasesState.screenData

    AppDropdownMenu(
        expanded = screenData.showDisplayTotal,
        onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListsDisplayTotal) },
        header = { Text(text = stringResource(R.string.shoppingLists_header_displayTotal)) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsAllTotal) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayAllTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayTotal == DisplayTotal.ALL) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsCompletedTotal) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayCompletedTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayTotal == DisplayTotal.COMPLETED) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsActiveTotal) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayActiveTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayTotal == DisplayTotal.ACTIVE) }
        )
    }
}

@Composable
private fun SortMenu(viewModel: PurchasesViewModel) {
    val screenData = viewModel.purchasesState.screenData

    AppDropdownMenu(
        expanded = screenData.showSort,
        onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListsSort) },
        header = { Text(text = stringResource(R.string.shoppingLists_header_sort)) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingListsByCreated) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByCreated)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.CREATED) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingListsByLastModified) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByLastModified)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.LAST_MODIFIED) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingListsByName) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByName)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.NAME) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.SortShoppingListsByTotal) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.TOTAL) }
        )
    }
}

@Composable
private fun CompletedMenu(viewModel: PurchasesViewModel) {
    val screenData = viewModel.purchasesState.screenData

    AppDropdownMenu(
        expanded = screenData.showDisplayCompleted,
        onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListsDisplayCompleted) },
        header = { Text(text = stringResource(R.string.shoppingLists_header_displayCompleted)) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsCompletedFirst) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayCompletedFirst)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayCompleted == DisplayCompleted.FIRST) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.DisplayShoppingListsCompletedLast) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayCompletedLast)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayCompleted == DisplayCompleted.LAST) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(PurchasesEvent.HideShoppingListsCompleted) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayCompletedHide)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayCompleted == DisplayCompleted.HIDE) }
        )
    }
}

@Composable
private fun ItemMenu(itemUid: String, viewModel: PurchasesViewModel) {
    val screenData = viewModel.purchasesState.screenData

    AppDropdownMenu(
        expanded = screenData.shoppingListMenuUid != null,
        onDismissRequest = { viewModel.onEvent(PurchasesEvent.HideShoppingListMenu) }
    ) {
        AppDropdownMenuItem(
            onClick = {
                val event = PurchasesEvent.MoveShoppingListToArchive(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = stringResource(R.string.purchases_action_moveShoppingListToArchive)) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = PurchasesEvent.MoveShoppingListToTrash(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = stringResource(R.string.purchases_action_moveShoppingListToTrash)) }
        )
    }
}