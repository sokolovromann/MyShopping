package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.FontSize
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

    BackHandler(enabled = screenData.selectedNames != null) {
        viewModel.onEvent(AutocompletesEvent.CancelSelectingAutocompletes)
    }

    AppScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (screenData.selectedNames == null) {
                AppTopAppBar(
                    title = { Text(text = stringResource(R.string.autocompletes_header_autocompletes)) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(AutocompletesEvent.ShowNavigationDrawer) }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.autocompletes_contentDescription_navigationIcon)
                            )
                        }
                    }
                )
            } else {
                AppTopAppBar(
                    title = { Text(text = screenData.selectedNames.size.toString()) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.onEvent(AutocompletesEvent.CancelSelectingAutocompletes) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.autocompletes_contentDescription_cancelSelectingAutocompletes)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(AutocompletesEvent.ClearAutocompletes) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_autocompletes_clear),
                                contentDescription = stringResource(R.string.autocompletes_contentDescription_clearAutocompletes)
                            )
                        }

                        if (screenData.location == AutocompleteLocation.PERSONAL) {
                            IconButton(onClick = { viewModel.onEvent(AutocompletesEvent.DeleteAutocompletes) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.autocompletes_contentDescription_deleteAutocompletes)
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.onEvent(AutocompletesEvent.SelectAllAutocompletes) }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_all_select_all),
                                contentDescription = stringResource(R.string.autocompletes_contentDescription_selectAllAutocompletes)
                            )
                        }
                    }
                )
            }
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
            if (screenData.selectedNames == null) {
                FloatingActionButton(onClick = { viewModel.onEvent(AutocompletesEvent.AddAutocomplete) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.autocompletes_contentDescription_addAutocompleteIcon),
                        tint = MaterialTheme.colors.onSecondary
                    )
                }
            }
        }
    ) { paddings ->
        AutocompletesGrid(
            modifier = Modifier.padding(paddings),
            screenState = screenData.screenState,
            multiColumns = screenData.multiColumns,
            smartphoneScreen = screenData.smartphoneScreen,
            map = screenData.autocompletes,
            topBar = {
                AutocompleteLocationContent(
                    location = screenData.location,
                    enabled = screenData.locationEnabled,
                    fontSize = screenData.fontSize.toButton().sp,
                    expanded = screenData.showLocation,
                    onExpanded = {
                        if (it) {
                            viewModel.onEvent(AutocompletesEvent.SelectAutocompleteLocation)
                        } else {
                            viewModel.onEvent(AutocompletesEvent.HideAutocompleteLocation)
                        }
                    },
                    onSelected = {
                        val event = AutocompletesEvent.ShowAutocompletes(it)
                        viewModel.onEvent(event)
                    }
                )
            },
            notFound = {
                Text(
                    text = stringResource(R.string.autocompletes_text_autocompletesNotFound),
                    fontSize = screenData.fontSize.toItemTitle().sp,
                    textAlign = TextAlign.Center
                )
            },
            fontSize = screenData.fontSize,
            onClick = {
                screenData.selectedNames?.let { names ->
                    val event = if (names.contains(it)) {
                        AutocompletesEvent.UnselectAutocomplete(it)
                    } else {
                        AutocompletesEvent.SelectAutocomplete(it)
                    }
                    viewModel.onEvent(event)
                }
            },
            onLongClick = {
                if (screenData.selectedNames == null) {
                    val event = AutocompletesEvent.SelectAutocomplete(it)
                    viewModel.onEvent(event)
                }
            },
            selectedNames = screenData.selectedNames
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AutocompletesGrid(
    modifier: Modifier = Modifier,
    screenState: ScreenState,
    multiColumns: Boolean,
    smartphoneScreen: Boolean,
    map: Map<UiText, AutocompleteItems>,
    topBar: @Composable RowScope.() -> Unit,
    notFound: @Composable (ColumnScope.() -> Unit)? = null,
    fontSize: FontSize,
    dropdownMenu: @Composable ((String) -> Unit)? = null,
    onClick: (String) -> Unit,
    onLongClick: (String) -> Unit,
    selectedNames: List<String>?
) {
    SmartphoneTabletAppGrid(
        modifier = modifier,
        screenState = screenState,
        multiColumns = multiColumns,
        multiColumnsSpace = true,
        smartphoneScreen = smartphoneScreen,
        topBar = topBar,
        notFound = notFound
    ) {
        val names = map.keys.toList()
        itemsIndexed(map.values.toList()) { index, item ->
            val nameToString = (names[index] as UiText.FromString).value
            val selected = selectedNames?.contains(nameToString) ?: false

            AppSurfaceItem(
                title = getAutocompleteItemTitleOrNull(names[index], fontSize),
                body = getAutocompleteItemBodyOrNull(item, fontSize),
                right = getAutocompleteItemRightOrNull(selected),
                dropdownMenu = { dropdownMenu?.let { it(nameToString) } },
                onClick = { onClick(nameToString) },
                onLongClick = { onLongClick(nameToString) },
                backgroundColor = getAppItemBackgroundColor(selected)
            )
        }
    }
}

@Composable
private fun AutocompleteLocationContent(
    modifier: Modifier = Modifier,
    location: AutocompleteLocation,
    enabled: Boolean,
    fontSize: TextUnit,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onSelected: (AutocompleteLocation) -> Unit
) {
    TextButton(
        modifier = Modifier
            .padding(AutocompleteLocationPaddings)
            .then(modifier),
        enabled = enabled,
        onClick = { onExpanded(true) }
    ) {
        Text(
            text = location.getText().asCompose(),
            fontSize = fontSize
        )
        AppDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpanded(false) },
            header = { Text(text = stringResource(R.string.autocompletes_header_location)) }
        ) {
            AppDropdownMenuItem(
                onClick = { onSelected(AutocompleteLocation.DEFAULT) },
                text = { Text(text = stringResource(R.string.autocompletes_action_selectDefaultLocation)) },
                right = { CheckmarkAppCheckbox(checked = location == AutocompleteLocation.DEFAULT) }
            )
            AppDropdownMenuItem(
                onClick = { onSelected(AutocompleteLocation.PERSONAL) },
                text = { Text(text = stringResource(R.string.autocompletes_action_selectPersonalLocation)) },
                right = { CheckmarkAppCheckbox(checked = location == AutocompleteLocation.PERSONAL) }
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

@Composable
private fun getAutocompleteItemBodyOrNull(
    autocompleteItems: AutocompleteItems,
    fontSize: FontSize
) = itemOrNull(enabled = true) {
    Column {
        if (autocompleteItems.isEmpty()) {
            Text(
                text = stringResource(R.string.autocompletes_body_dataNotFound),
                fontSize = fontSize.toItemBody().sp
            )
        } else {
            if (autocompleteItems.brandsToText() != UiText.Nothing) {
                Text(
                    text = autocompleteItems.brandsToText().asCompose(),
                    fontSize = fontSize.toItemBody().sp
                )
            }
            if (autocompleteItems.sizesToText() != UiText.Nothing) {
                Text(
                    text = autocompleteItems.sizesToText().asCompose(),
                    fontSize = fontSize.toItemBody().sp
                )
            }
            if (autocompleteItems.colorsToText() != UiText.Nothing) {
                Text(
                    text = autocompleteItems.colorsToText().asCompose(),
                    fontSize = fontSize.toItemBody().sp
                )
            }
            if (autocompleteItems.manufacturersToText() != UiText.Nothing) {
                Text(
                    text = autocompleteItems.manufacturersToText().asCompose(),
                    fontSize = fontSize.toItemBody().sp
                )
            }
            if (autocompleteItems.quantitiesToText() != UiText.Nothing) {
                Text(
                    text = autocompleteItems.quantitiesToText().asCompose(),
                    fontSize = fontSize.toItemBody().sp
                )
            }
            if (autocompleteItems.pricesToText() != UiText.Nothing) {
                Text(
                    text = autocompleteItems.pricesToText().asCompose(),
                    fontSize = fontSize.toItemBody().sp
                )
            }
            if (autocompleteItems.discountsToText() != UiText.Nothing) {
                Text(
                    text = autocompleteItems.discountsToText().asCompose(),
                    fontSize = fontSize.toItemBody().sp
                )
            }
            if (autocompleteItems.totalsToText() != UiText.Nothing) {
                Text(
                    text = autocompleteItems.totalsToText().asCompose(),
                    fontSize = fontSize.toItemBody().sp
                )
            }
        }
    }
}

@Composable
private fun getAutocompleteItemRightOrNull(
    selected: Boolean
) = itemOrNull(enabled = selected) {
    CheckmarkAppCheckbox(
        checked = true,
        checkmarkColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    )
}

private val AutocompleteItemTextPaddings = PaddingValues(vertical = 4.dp)
private val AutocompleteLocationPaddings = PaddingValues(
    horizontal = 8.dp
)