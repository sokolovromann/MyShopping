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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.AutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.viewmodel.AutocompletesViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.AutocompletesEvent

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

                AutocompletesScreenEvent.ShowPurchases -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Purchases.purchasesScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                AutocompletesScreenEvent.ShowArchive -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Archive.archiveScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                AutocompletesScreenEvent.ShowTrash -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Trash.trashScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                AutocompletesScreenEvent.ShowSettings -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Settings.settingsScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

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

    AppSystemUi(systemUiController = systemUiController)

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
    TopAppBar(
        title = { Text(text = viewModel.topBarState.value.title.text.asCompose()) },
        navigationIcon = {
            IconButton(onClick = { viewModel.onEvent(AutocompletesEvent.ShowNavigationDrawer) }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.autocompletes_contentDescription_navigationIcon),
                    tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                )
            }
        }
    )
}

@Composable
private fun DrawerContent(viewModel: AutocompletesViewModel) {
    AppDrawerContent(
        selected = UiRoute.Autocompletes,
        onItemClick = {
            val event = AutocompletesEvent.SelectNavigationItem(it)
            viewModel.onEvent(event)
        }
    )
}

@Composable
private fun FloatingActionButton(viewModel: AutocompletesViewModel) {
    FloatingActionButton(onClick = { viewModel.onEvent(AutocompletesEvent.AddAutocomplete) }) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.autocompletes_contentDescription_addAutocompleteIcon),
            tint = MaterialTheme.colors.onSecondary
        )
    }
}

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

@Composable
private fun AutocompletesShowing(data: ListData<AutocompleteItem>, viewModel: AutocompletesViewModel) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 4.dp)
        .verticalScroll(scrollState)
    ) {
        AutocompletesBar(viewModel)
        AppGrid(multiColumns = data.multiColumns) {
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

    AppDropdownMenu(
        expanded = sortData.expandedMenu,
        onDismissRequest = { viewModel.onEvent(AutocompletesEvent.HideAutocompletesSort) },
        header = { Text(text = menu.title.text.asCompose()) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(AutocompletesEvent.SortAutocompletesByCreated) },
            text = { Text(text = menu.byCreatedBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byCreatedSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(AutocompletesEvent.SortAutocompletesByName) },
            text = { Text(text = menu.byNameBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.byNameSelected.selected) }
        )
    }
}

@Composable
private fun AutocompletesItem(item: AutocompleteItem, viewModel: AutocompletesViewModel) {
    AppSurfaceItem(
        title = itemTitleOrNull(item),
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

    AppDropdownMenu(
        expanded = itemMenuData.itemUid == itemUid,
        onDismissRequest = { viewModel.onEvent(AutocompletesEvent.HideAutocompleteMenu) }
    ) {
        AppDropdownMenuItem(
            onClick = {
                val event = AutocompletesEvent.EditAutocomplete(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = menu.editBody.text.asCompose()) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = AutocompletesEvent.DeleteAutocomplete(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = menu.deleteBody.text.asCompose()) }
        )
    }
}

@Composable
private fun itemTitleOrNull(item: AutocompleteItem): @Composable (() -> Unit)? {
    val title = item.title
    return itemOrNull(enabled = title.isTextShowing()) {
        Text(
            text = title.text.asCompose(),
            fontSize = title.fontSize
        )
    }
}