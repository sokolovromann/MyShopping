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
import ru.sokolovromann.myshopping.ui.compose.event.ArchiveScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.utils.getAscendingIcon
import ru.sokolovromann.myshopping.ui.utils.getShoppingListsText
import ru.sokolovromann.myshopping.ui.utils.toButton
import ru.sokolovromann.myshopping.ui.utils.toItemTitle
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
    val screenData = viewModel.archiveState.screenData
    AppBottomAppBar {
        TextButton(
            onClick = { viewModel.onEvent(ArchiveEvent.SelectShoppingListsDisplayTotal)}
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
        when (viewModel.archiveState.screenData.screenState) {
            ScreenState.Nothing -> ArchiveNotFound(viewModel)
            ScreenState.Loading -> ArchiveLoading()
            ScreenState.Showing -> ArchiveShowing(viewModel)
            ScreenState.Saving -> {}
        }
    }
}

@Composable
private fun ArchiveShowing(viewModel: ArchiveViewModel) {
    val screenData = viewModel.archiveState.screenData
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 4.dp)
        .verticalScroll(scrollState)
    ) {
        ArchiveBar(viewModel)
        AppGrid(multiColumns = screenData.multiColumns) {
            screenData.shoppingLists.forEach { item -> ArchiveItem(item, viewModel)}
        }
        Spacer(modifier = Modifier.height(128.dp))
    }
}

@Composable
private fun ArchiveBar(viewModel: ArchiveViewModel) {
    val screenData = viewModel.archiveState.screenData
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            TextButton(onClick = { viewModel.onEvent(ArchiveEvent.SelectShoppingListsSort) }) {
                Text(
                    text = screenData.sort.getShoppingListsText().asCompose(),
                    fontSize = screenData.fontSize.toButton().sp
                )
                SortMenu(viewModel)
            }
            IconButton(onClick = { viewModel.onEvent(ArchiveEvent.InvertShoppingListsSort) }) {
                Icon(
                    painter = screenData.sort.getAscendingIcon().asPainter() ?: return@IconButton,
                    contentDescription = stringResource(R.string.shoppingLists_contentDescription_sortAscendingIcon),
                    tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                )
            }
        }
        IconButton(onClick = { viewModel.onEvent(ArchiveEvent.SelectShoppingListsDisplayCompleted) }) {
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
private fun ArchiveNotFound(viewModel: ArchiveViewModel) {
    val screenData = viewModel.archiveState.screenData
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.archive_text_shoppingListsNotFound),
            fontSize = screenData.fontSize.toItemTitle().sp,
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
    val screenData = viewModel.archiveState.screenData

    AppDropdownMenu(
        expanded = screenData.showDisplayTotal,
        onDismissRequest = { viewModel.onEvent(ArchiveEvent.HideShoppingListsDisplayTotal) },
        header = { Text(text = stringResource(R.string.shoppingLists_header_displayTotal)) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.DisplayShoppingListsAllTotal) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayAllTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayTotal == DisplayTotal.ALL) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.DisplayShoppingListsCompletedTotal) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayCompletedTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayTotal == DisplayTotal.COMPLETED) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.DisplayShoppingListsActiveTotal) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayActiveTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayTotal == DisplayTotal.ACTIVE) }
        )
    }
}

@Composable
private fun SortMenu(viewModel: ArchiveViewModel) {
    val screenData = viewModel.archiveState.screenData

    AppDropdownMenu(
        expanded = screenData.showSort,
        onDismissRequest = { viewModel.onEvent(ArchiveEvent.HideShoppingListsSort) },
        header = { Text(text = stringResource(R.string.shoppingLists_header_sort)) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.SortShoppingListsByCreated) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByCreated)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.CREATED) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.SortShoppingListsByLastModified) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByLastModified)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.LAST_MODIFIED) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.SortShoppingListsByName) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByName)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.NAME) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.SortShoppingListsByTotal) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_sortByTotal)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.TOTAL) }
        )
    }
}

@Composable
private fun CompletedMenu(viewModel: ArchiveViewModel) {
    val screenData = viewModel.archiveState.screenData

    AppDropdownMenu(
        expanded = screenData.showDisplayCompleted,
        onDismissRequest = { viewModel.onEvent(ArchiveEvent.HideShoppingListsDisplayCompleted) },
        header = { Text(text = stringResource(R.string.shoppingLists_header_displayCompleted)) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.DisplayShoppingListsCompletedFirst) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayCompletedFirst)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayCompleted == DisplayCompleted.FIRST) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.DisplayShoppingListsCompletedLast) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayCompletedLast)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayCompleted == DisplayCompleted.LAST) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(ArchiveEvent.HideShoppingListsCompleted) },
            text = { Text(text = stringResource(R.string.shoppingLists_action_displayCompletedHide)) },
            after = { CheckmarkAppCheckbox(checked = screenData.displayCompleted == DisplayCompleted.HIDE) }
        )
    }
}

@Composable
private fun ItemMenu(itemUid: String, viewModel: ArchiveViewModel) {
    val screenData = viewModel.archiveState.screenData

    AppDropdownMenu(
        expanded = itemUid == screenData.shoppingListMenuUid,
        onDismissRequest = { viewModel.onEvent(ArchiveEvent.HideShoppingListMenu) }
    ) {
        AppDropdownMenuItem(
            onClick = {
                val event = ArchiveEvent.MoveShoppingListToPurchases(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = stringResource(R.string.archive_action_moveShoppingListToPurchases)) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = ArchiveEvent.MoveShoppingListToTrash(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text =  stringResource(R.string.archive_action_moveShoppingListToTrash)) }
        )
    }
}