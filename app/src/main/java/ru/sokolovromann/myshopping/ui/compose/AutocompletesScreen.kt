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
import ru.sokolovromann.myshopping.ui.compose.event.AutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.viewmodel.AutocompletesViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.AutocompletesEvent

@ExperimentalFoundationApi
@Composable
fun AutocompletesScreen(
    navController: NavController,
    viewModel: AutocompletesViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                AutocompletesScreenEvent.AddAutocomplete -> navController.navigate(
                    route = UiRoute.Autocompletes.addAutocompletesScreen
                )

                is AutocompletesScreenEvent.EditAutocomplete -> navController.navigate(
                    route = UiRoute.Autocompletes.editAutocompleteScreen(it.uid)
                )

                AutocompletesScreenEvent.ShowBackScreen -> navController.popBackStack()

                AutocompletesScreenEvent.ShowPurchases -> navController.navigateWithDrawerOption(
                    route = UiRoute.Purchases.purchasesScreen
                )

                AutocompletesScreenEvent.ShowArchive -> navController.navigateWithDrawerOption(
                    route = UiRoute.Archive.archiveScreen
                )

                AutocompletesScreenEvent.ShowTrash -> navController.navigateWithDrawerOption(
                    route = UiRoute.Trash.trashScreen
                )

                AutocompletesScreenEvent.ShowSettings -> navController.navigateWithDrawerOption(
                    route = UiRoute.Settings.settingsScreen
                )

                AutocompletesScreenEvent.ShowNavigationDrawer -> coroutineScope.launch {
                    scaffoldState.drawerState.open()
                }

                AutocompletesScreenEvent.HideNavigationDrawer -> coroutineScope.launch {
                    scaffoldState.drawerState.close()
                }
            }
        }
    }

    BackHandler(enabled = scaffoldState.drawerState.isOpen) {
        viewModel.onEvent(AutocompletesEvent.HideNavigationDrawer)
    }

    AppSystemUi(
        systemUiController = systemUiController,
        data = viewModel.systemUiState.value
    )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(viewModel) },
        drawerContent = { DrawerContent(viewModel) },
        floatingActionButton = { FloatingActionButton(viewModel) },
        content = { paddingValues -> Content(paddingValues, viewModel) }
    )
}

@Composable
private fun TopBar(viewModel: AutocompletesViewModel) {
    AppTopBar(
        data = viewModel.topBarState.value,
        onNavigationIconClick = { viewModel.onEvent(AutocompletesEvent.ShowNavigationDrawer) }
    )
}

@Composable
private fun DrawerContent(viewModel: AutocompletesViewModel) {
    AppNavigationDrawer(
        data = viewModel.navigationDrawerState.value,
        onClick = {
            val event = AutocompletesEvent.SelectNavigationItem(it.route)
            viewModel.onEvent(event)
        }
    )
}

@Composable
private fun FloatingActionButton(viewModel: AutocompletesViewModel) {
    AppFloatingActionButton(
        data = viewModel.floatingActionButtonState.value,
        onClick = { viewModel.onEvent(AutocompletesEvent.AddAutocomplete) }
    )
}

@ExperimentalFoundationApi
@Composable
private fun Content(paddingValues: PaddingValues, viewModel: AutocompletesViewModel) {
    Box(modifier = Modifier.padding(paddingValues)) {
        val listData = viewModel.autocompletesState.currentData
        when (listData.result) {
            ListResult.Showing -> AutocompletesShowing(listData, viewModel)
            ListResult.NotFound -> AutocompletesNotFound(listData.notFoundText)
            ListResult.Loading -> AutocompletesLoading()
            ListResult.Nothing -> {}
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun AutocompletesShowing(data: ListData<AutocompleteItem>, viewModel: AutocompletesViewModel) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 4.dp)
        .verticalScroll(scrollState)
    ) {
        AutocompletesBar(viewModel)
        AppGrid(data = data) {
            data.items.forEach { item -> AutocompletesItem(item, viewModel)}
        }
        Spacer(modifier = Modifier.height(128.dp))
    }
}

@Composable
private fun AutocompletesBar(viewModel: AutocompletesViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            TextButton(
                onClick = { viewModel.onEvent(AutocompletesEvent.SelectAutocompletesSort) }
            ) {
                AppText(data = viewModel.sortState.currentData.text)
                SortMenu(viewModel)
            }
            IconButton(onClick = { viewModel.onEvent(AutocompletesEvent.InvertAutocompletesSort) }) {
                AppIcon(data = viewModel.sortAscendingState.value)
            }
        }
    }
}

@Composable
private fun AutocompletesNotFound(data: TextData) {
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
private fun AutocompletesLoading() {
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
private fun SortMenu(viewModel: AutocompletesViewModel) {
    val sortData = viewModel.sortState.currentData
    val menu = sortData.menu ?: return

    DropdownMenu(
        expanded = sortData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(AutocompletesEvent.HideAutocompletesSort) }
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
            onClick = { viewModel.onEvent(AutocompletesEvent.SortAutocompletesByCreated) },
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.byNameSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.byNameBody
                )
            },
            onClick = { viewModel.onEvent(AutocompletesEvent.SortAutocompletesByName) },
        )
    }
}

@ExperimentalFoundationApi
@Composable
private fun AutocompletesItem(item: AutocompleteItem, viewModel: AutocompletesViewModel) {
    AppSurfaceItem(
        title = itemTitleOrNull(item),
        body = itemBodyOrNull(item),
        dropdownMenu = { ItemMenu(item.uid, viewModel) },
        onClick = {},
        onLongClick = {
            val event = AutocompletesEvent.ShowAutocompleteMenu(item.uid)
            viewModel.onEvent(event)
        }
    )
}

@Composable
private fun ItemMenu(itemUid: String, viewModel: AutocompletesViewModel) {
    val itemMenuData = viewModel.itemMenuState.currentData
    val menu = itemMenuData.menu ?: return

    DropdownMenu(
        expanded = itemMenuData.itemUid == itemUid,
        onDismissRequest = { viewModel.onEvent(AutocompletesEvent.HideAutocompleteMenu) }
    ) {
        AppMenuItem(
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    data = menu.editBody
                )
            },
            onClick = {
                val event = AutocompletesEvent.EditAutocomplete(itemUid)
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
                val event = AutocompletesEvent.DeleteAutocomplete(itemUid)
                viewModel.onEvent(event)
            }
        )
    }
}

@Composable
private fun itemTitleOrNull(item: AutocompleteItem): @Composable (() -> Unit)? {
    if (item.title.isTextHiding()) {
        return null
    }

    return {
        AppText(data = item.title)
    }
}

@Composable
private fun itemBodyOrNull(item: AutocompleteItem): @Composable (() -> Unit)? {
    return null
}