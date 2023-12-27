package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.items
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
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.AutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.model.AutocompleteItem
import ru.sokolovromann.myshopping.ui.model.AutocompleteLocation
import ru.sokolovromann.myshopping.ui.model.SelectedValue
import ru.sokolovromann.myshopping.ui.model.UiFontSize
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.viewmodel.AutocompletesViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.AutocompletesEvent

@Composable
fun AutocompletesScreen(
    navController: NavController,
    viewModel: AutocompletesViewModel = hiltViewModel()
) {
    val state = viewModel.autocompletesState
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

    BackHandler(enabled = state.selectedNames != null) {
        viewModel.onEvent(AutocompletesEvent.CancelSelectingAutocompletes)
    }

    AppScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (state.selectedNames == null) {
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
                    title = { Text(text = state.selectedNames?.size.toString()) },
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

                        if (state.locationValue.selected == AutocompleteLocation.PERSONAL) {
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
            if (state.selectedNames == null) {
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
            screenState = ScreenState.create(
                waiting = state.waiting,
                notFound = state.isNotFound()
            ),
            multiColumns = state.multiColumns,
            smartphoneScreen = state.smartphoneScreen,
            autocompletes = state.autocompletes,
            topBar = {
                AutocompleteLocationContent(
                    locationValue = state.locationValue,
                    enabled = state.locationEnabled,
                    fontSize = state.fontSize.button.sp,
                    expanded = state.expandedLocation,
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
                    fontSize = state.fontSize.itemTitle.sp,
                    textAlign = TextAlign.Center
                )
            },
            fontSize = state.fontSize,
            onClick = {
                state.selectedNames?.let { names ->
                    val event = if (names.contains(it)) {
                        AutocompletesEvent.UnselectAutocomplete(it)
                    } else {
                        AutocompletesEvent.SelectAutocomplete(it)
                    }
                    viewModel.onEvent(event)
                }
            },
            onLongClick = {
                if (state.selectedNames == null) {
                    val event = AutocompletesEvent.SelectAutocomplete(it)
                    viewModel.onEvent(event)
                }
            },
            selectedNames = state.selectedNames
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
    autocompletes: List<AutocompleteItem>,
    topBar: @Composable RowScope.() -> Unit,
    notFound: @Composable (ColumnScope.() -> Unit)? = null,
    fontSize: UiFontSize,
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
        items(autocompletes) {
            val nameToString = it.name.asCompose()
            val selected = selectedNames?.contains(nameToString) ?: false

            AppSurfaceItem(
                title = getAutocompleteItemTitleOrNull(it.name, fontSize),
                body = getAutocompleteItemBodyOrNull(it, fontSize),
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
    locationValue: SelectedValue<AutocompleteLocation>,
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
            text = locationValue.text.asCompose(),
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
                right = { CheckmarkAppCheckbox(checked = locationValue.selected == AutocompleteLocation.DEFAULT) }
            )
            AppDropdownMenuItem(
                onClick = { onSelected(AutocompleteLocation.PERSONAL) },
                text = { Text(text = stringResource(R.string.autocompletes_action_selectPersonalLocation)) },
                right = { CheckmarkAppCheckbox(checked = locationValue.selected == AutocompleteLocation.PERSONAL) }
            )
        }
    }
}

@Composable
private fun getAutocompleteItemTitleOrNull(
    name: UiString,
    fontSize: UiFontSize
) = itemOrNull(enabled = name.asCompose().isNotEmpty()) {
    Text(
        modifier = Modifier.padding(AutocompleteItemTextPaddings),
        text = name.asCompose(),
        fontSize = fontSize.itemTitle.sp
    )
}

@Composable
private fun getAutocompleteItemBodyOrNull(
    autocompleteItems: AutocompleteItem,
    fontSize: UiFontSize
) = itemOrNull(enabled = true) {
    Column {
        if (autocompleteItems.isNotFound()) {
            Text(
                text = stringResource(R.string.autocompletes_body_dataNotFound),
                fontSize = fontSize.itemBody.sp
            )
        } else {
            if (autocompleteItems.brands.isNotEmpty()) {
                Text(
                    text = autocompleteItems.brands.asCompose(),
                    fontSize = fontSize.itemBody.sp
                )
            }
            if (autocompleteItems.sizes.isNotEmpty()) {
                Text(
                    text = autocompleteItems.sizes.asCompose(),
                    fontSize = fontSize.itemBody.sp
                )
            }
            if (autocompleteItems.colors.isNotEmpty()) {
                Text(
                    text = autocompleteItems.colors.asCompose(),
                    fontSize = fontSize.itemBody.sp
                )
            }
            if (autocompleteItems.manufacturers.isNotEmpty()) {
                Text(
                    text = autocompleteItems.manufacturers.asCompose(),
                    fontSize = fontSize.itemBody.sp
                )
            }
            if (autocompleteItems.quantities.isNotEmpty()) {
                Text(
                    text = autocompleteItems.quantities.asCompose(),
                    fontSize = fontSize.itemBody.sp
                )
            }
            if (autocompleteItems.prices.isNotEmpty()) {
                Text(
                    text = autocompleteItems.prices.asCompose(),
                    fontSize = fontSize.itemBody.sp
                )
            }
            if (autocompleteItems.discounts.isNotEmpty()) {
                Text(
                    text = autocompleteItems.discounts.asCompose(),
                    fontSize = fontSize.itemBody.sp
                )
            }
            if (autocompleteItems.totals.isNotEmpty()) {
                Text(
                    text = autocompleteItems.totals.asCompose(),
                    fontSize = fontSize.itemBody.sp
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