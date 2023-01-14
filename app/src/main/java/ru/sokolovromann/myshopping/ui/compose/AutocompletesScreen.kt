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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.SortBy
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.AutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.utils.*
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
        val screenData = viewModel.autocompletesState.screenData
        when (screenData.screenState) {
            ScreenState.Nothing -> AutocompletesNotFound(viewModel)
            ScreenState.Loading -> AutocompletesLoading()
            ScreenState.Showing -> AutocompletesShowing(viewModel)
            ScreenState.Saving -> {}
        }
    }
}

@Composable
private fun AutocompletesShowing(viewModel: AutocompletesViewModel) {
    val screenData = viewModel.autocompletesState.screenData
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 4.dp)
        .verticalScroll(scrollState)
    ) {
        AutocompletesBar(viewModel)
        AppGrid(multiColumns = screenData.multiColumns) {
            screenData.autocompletes.forEach { item -> AutocompletesItem(item, viewModel)}
        }
        Spacer(modifier = Modifier.height(128.dp))
    }
}

@Composable
private fun AutocompletesBar(viewModel: AutocompletesViewModel) {
    val screenData = viewModel.autocompletesState.screenData
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
                Text(
                    text = screenData.sort.getAutocompletesText().asCompose(),
                    fontSize = screenData.fontSize.toButton().sp
                )
                SortMenu(viewModel)
            }
            IconButton(onClick = { viewModel.onEvent(AutocompletesEvent.InvertAutocompletesSort) }) {
                Icon(
                    painter = screenData.sort.getAscendingIcon().asPainter() ?: return@IconButton,
                    contentDescription = stringResource(R.string.autocompletes_contentDescription_sortAscendingIcon),
                    tint = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
                )
            }
        }
    }
}

@Composable
private fun AutocompletesNotFound(viewModel: AutocompletesViewModel) {
    val screenData = viewModel.autocompletesState.screenData
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.autocompletes_text_autocompletesNotFound),
            fontSize = screenData.fontSize.toItemTitle().sp,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onBackground
        )
    }
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
    val screenData = viewModel.autocompletesState.screenData

    AppDropdownMenu(
        expanded = screenData.showSort,
        onDismissRequest = { viewModel.onEvent(AutocompletesEvent.HideAutocompletesSort) },
        header = { Text(text = stringResource(R.string.autocompletes_header_sort)) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(AutocompletesEvent.SortAutocompletesByCreated) },
            text = { Text(text = stringResource(R.string.autocompletes_action_sortByCreated)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.CREATED) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(AutocompletesEvent.SortAutocompletesByName) },
            text = { Text(text = stringResource(R.string.autocompletes_action_sortByName)) },
            after = { CheckmarkAppCheckbox(checked = screenData.sort.sortBy == SortBy.NAME) }
        )
    }
}

@Composable
private fun AutocompletesItem(item: AutocompleteItem, viewModel: AutocompletesViewModel) {
    AppSurfaceItem(
        title = itemTitleOrNull(item,viewModel),
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
    val screenData = viewModel.autocompletesState.screenData

    AppDropdownMenu(
        expanded = itemUid == screenData.autocompleteMenuUid,
        onDismissRequest = { viewModel.onEvent(AutocompletesEvent.HideAutocompleteMenu) }
    ) {
        AppDropdownMenuItem(
            onClick = {
                val event = AutocompletesEvent.EditAutocomplete(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = stringResource(R.string.autocompletes_action_editAutocomplete)) }
        )
        AppDropdownMenuItem(
            onClick = {
                val event = AutocompletesEvent.DeleteAutocomplete(itemUid)
                viewModel.onEvent(event)
            },
            text = { Text(text = stringResource(R.string.autocompletes_action_deleteAutocomplete)) }
        )
    }
}

@Composable
private fun itemTitleOrNull(item: AutocompleteItem, viewModel: AutocompletesViewModel): @Composable (() -> Unit)? {
    val screenData = viewModel.autocompletesState.screenData
    val title = item.nameText
    return itemOrNull(enabled = title != UiText.Nothing) {
        Text(
            text = title.asCompose(),
            fontSize = screenData.fontSize.toItemTitle().sp
        )
    }
}