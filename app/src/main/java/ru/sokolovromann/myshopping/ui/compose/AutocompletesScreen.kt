package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.FontSize
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
    val screenData = viewModel.autocompletesState.screenData
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

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

    AppGridScaffold(
        scaffoldState = scaffoldState,
        screenState = screenData.screenState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.autocompletes_header_autocompletes)) },
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
        },
        drawerContent = {
            AppDrawerContent(
                selected = UiRoute.Autocompletes,
                onItemClick = {
                    val event = AutocompletesEvent.SelectNavigationItem(it)
                    viewModel.onEvent(event)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(AutocompletesEvent.AddAutocomplete) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.autocompletes_contentDescription_addAutocompleteIcon),
                    tint = MaterialTheme.colors.onSecondary
                )
            }
        },
        loadingContent = {
            AppLoadingContent(indicator = { CircularProgressIndicator() })
        },
        notFoundContent = {
            AppNotFoundContent {
                Text(
                    text = stringResource(R.string.autocompletes_text_autocompletesNotFound),
                    fontSize = screenData.fontSize.toItemTitle().sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        gridContent = {
            AutocompletesGrid(
                multiColumns = screenData.multiColumns,
                items = screenData.autocompletes,
                fontSize = screenData.fontSize,
                dropdownMenu = {
                    AppDropdownMenu(
                        expanded = it == screenData.autocompleteMenuUid,
                        onDismissRequest = { viewModel.onEvent(AutocompletesEvent.HideAutocompleteMenu) }
                    ) {
                        AppDropdownMenuItem(
                            onClick = {
                                val event = AutocompletesEvent.EditAutocomplete(it)
                                viewModel.onEvent(event)
                            },
                            text = { Text(text = stringResource(R.string.autocompletes_action_editAutocomplete)) }
                        )
                        AppDropdownMenuItem(
                            onClick = {
                                val event = AutocompletesEvent.DeleteAutocomplete(it)
                                viewModel.onEvent(event)
                            },
                            text = { Text(text = stringResource(R.string.autocompletes_action_deleteAutocomplete)) }
                        )
                    }
                },
                onClick = {},
                onLongClick = {
                    val event = AutocompletesEvent.ShowAutocompleteMenu(it)
                    viewModel.onEvent(event)
                }
            )
        },
        gridMultiColumnsSpace = screenData.multiColumns
    )
}

@Composable
private fun AutocompletesGrid(
    modifier: Modifier = Modifier,
    multiColumns: Boolean,
    items: List<AutocompleteItem>,
    fontSize: FontSize,
    dropdownMenu: @Composable ((String) -> Unit)? = null,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit
) {
    AppGrid(
        modifier = modifier,
        multiColumns = multiColumns
    ) {
        val itemModifier = if (multiColumns) {
            Modifier
        } else {
            Modifier.padding(AutocompleteItemNotMultiColumnsPaddings)
        }

        items.forEach { item ->
            AppMultiColumnsItem(
                modifier = itemModifier,
                multiColumns = multiColumns,
                title = getAutocompleteItemTitleOrNull(item.nameText, fontSize),
                dropdownMenu = { dropdownMenu?.let { it(item.uid) } },
                onClick = { onClick(item.uid) },
                onLongClick = { onLongClick(item.uid) }
            )
        }
    }
}

@Composable
private fun getAutocompleteItemTitleOrNull(
    name: UiText,
    fontSize: FontSize
) = itemOrNull(enabled = name.asCompose().isNotEmpty()) {
    Text(
        modifier = Modifier.padding(AutocompleteItemTextPaddings),
        text = name.asCompose(),
        fontSize = fontSize.toItemTitle().sp
    )
}

private val AutocompleteItemTextPaddings = PaddingValues(vertical = 4.dp)
private val AutocompleteItemNotMultiColumnsPaddings = PaddingValues(horizontal = 8.dp)