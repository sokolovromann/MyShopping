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
import ru.sokolovromann.myshopping.ui.compose.event.TrashScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.utils.getAscendingIcon
import ru.sokolovromann.myshopping.ui.utils.getShoppingListsText
import ru.sokolovromann.myshopping.ui.utils.toButton
import ru.sokolovromann.myshopping.ui.utils.toItemTitle
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
    val screenData = viewModel.trashState.screenData
    AppBottomAppBar {
        TextButton(
            onClick = { viewModel.onEvent(TrashEvent.SelectShoppingListsDisplayTotal)}
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
        val screenData = viewModel.trashState.screenData
        when (screenData.screenState) {
            ScreenState.Nothing -> TrashNotFound(viewModel)
            ScreenState.Loading -> TrashLoading()
            ScreenState.Showing -> TrashShowing(viewModel)
            ScreenState.Saving -> {}
        }
    }
}

@Composable
private fun TrashShowing(viewModel: TrashViewModel) {
    val screenData = viewModel.trashState.screenData
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 4.dp)
        .verticalScroll(scrollState)
    ) {
        TrashBar(viewModel)
        AppGrid(multiColumns = screenData.multiColumns) {
            screenData.shoppingLists.forEach { item -> TrashItem(item, viewModel)}
        }
        Spacer(modifier = Modifier.height(128.dp))
    }
}

@Composable
private fun TrashBar(viewModel: TrashViewModel) {
    val screenData = viewModel.trashState.screenData
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            TextButton(onClick = { viewModel.onEvent(TrashEvent.SelectShoppingListsSort) }) {
                Text(
                    text = screenData.sort.getShoppingListsText().asCompose(),
                    fontSize = screenData.fontSize.toButton().sp
                )
                SortMenu(viewModel)
            }
            IconButton(onClick = { viewModel.onEvent(TrashEvent.InvertShoppingListsSort) }) {
                Icon(
                    painter = screenData.sort.getAscendingIcon().asPainter() ?: return@IconButton,
                    contentDescription = stringResource(R.string.shoppingLists_contentDescription_sortAscendingIcon),
                    tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                )
            }
        }
        IconButton(onClick = { viewModel.onEvent(TrashEvent.SelectShoppingListsDisplayCompleted) }) {
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
private fun TrashNotFound(viewModel: TrashViewModel) {
    val screenData = viewModel.trashState.screenData
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.trash_text_shoppingListsNotFound),
            fontSize = screenData.fontSize.toItemTitle().sp,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onBackground
        )
    }
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
    ShoppingListSurfaceItem(
        shoppingListItem = item,
        fontSize = FontSize.MEDIUM,
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
    val screenData = viewModel.trashState.screenData

    AppDropdownMenu(
        expanded = screenData.showDisplayTotal,
        onDismissRequest = { viewModel.onEvent(TrashEvent.HideShoppingListsDisplayTotal) },
        header = { Text(text = stringResource(R.string.shoppingLists_header_displayTotal)) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsAllTotal) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayAllTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayTotal == DisplayTotal.ALL) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsCompletedTotal) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayCompletedTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayTotal == DisplayTotal.COMPLETED) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsActiveTotal) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayActiveTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayTotal == DisplayTotal.ACTIVE) }
        )
    }
}

@Composable
private fun SortMenu(viewModel: TrashViewModel) {
    val screenData = viewModel.trashState.screenData

    AppDropdownMenu(
        expanded = screenData.showSort,
        onDismissRequest = { viewModel.onEvent(TrashEvent.HideShoppingListsSort) },
        header = { Text(text = stringResource(R.string.shoppingLists_header_sort)) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.SortShoppingListsByCreated) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByCreated)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.CREATED) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.SortShoppingListsByLastModified) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByLastModified)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.LAST_MODIFIED) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.SortShoppingListsByName) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByName)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.NAME) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.SortShoppingListsByTotal) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.TOTAL) }
        )
    }
}

@Composable
private fun CompletedMenu(viewModel: TrashViewModel) {
    val screenData = viewModel.trashState.screenData

    AppDropdownMenu(
        expanded = screenData.showDisplayCompleted,
        onDismissRequest = { viewModel.onEvent(TrashEvent.HideShoppingListsDisplayCompleted) },
        header = { Text(text = stringResource(R.string.shoppingLists_header_displayCompleted)) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsCompletedFirst) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayCompletedFirst)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayCompleted == DisplayCompleted.FIRST) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.DisplayShoppingListsCompletedLast) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayCompletedLast)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayCompleted == DisplayCompleted.LAST) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(TrashEvent.HideShoppingListsCompleted) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayCompletedHide)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayCompleted == DisplayCompleted.HIDE) }
        )
    }
}

@Composable
private fun ItemMenu(itemUid: String, viewModel: TrashViewModel) {
    val screenData = viewModel.trashState.screenData

    AppDropdownMenu(
        expanded = itemUid == screenData.shoppingListMenuUid,
        onDismissRequest = { viewModel.onEvent(TrashEvent.HideShoppingListMenu) }
    ) {
        AppDropdownMenuItem(
            onClick = {
                val event = TrashEvent.MoveShoppingListToPurchases(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = stringResource(R.string.trash_action_moveShoppingListToPurchases)) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = TrashEvent.MoveShoppingListToArchive(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = stringResource(R.string.trash_action_moveShoppingListToArchive)) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = TrashEvent.DeleteShoppingList(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = stringResource(R.string.trash_action_deleteShoppingList)) }
        )
    }
}