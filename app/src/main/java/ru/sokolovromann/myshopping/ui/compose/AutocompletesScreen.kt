package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.ui.DrawerScreen
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.AutocompletesScreenEvent
import ru.sokolovromann.myshopping.ui.model.AutocompleteItem
import ru.sokolovromann.myshopping.ui.model.UiIcon
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.viewmodel.AutocompletesViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.AutocompletesEvent
import ru.sokolovromann.myshopping.utils.UID

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
                AutocompletesScreenEvent.OnShowBackScreen -> {
                    navController.popBackStack()
                }

                AutocompletesScreenEvent.OnShowAddAutocompleteScreen -> navController.navigate(
                    route = UiRoute.Autocompletes.addAutocompletesScreen
                )

                is AutocompletesScreenEvent.OnShowEditAutocompleteScreen -> navController.navigate(
                    route = UiRoute.Autocompletes.editAutocompleteScreen(it.uid)
                )

                is AutocompletesScreenEvent.OnDrawerScreenSelected -> {
                    navController.navigateWithDrawerOption(route = it.drawerScreen.getScreen())
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                is AutocompletesScreenEvent.OnSelectDrawerScreen -> coroutineScope.launch {
                    if (it.display) {
                        scaffoldState.drawerState.open()
                    } else {
                        scaffoldState.drawerState.close()
                    }
                }
            }
        }
    }

    BackHandler(enabled = scaffoldState.drawerState.isOpen) {
        viewModel.onEvent(AutocompletesEvent.OnSelectDrawerScreen(false))
    }

    BackHandler(enabled = state.selectedUids != null) {
        viewModel.onEvent(AutocompletesEvent.OnAllAutocompletesSelected(false))
    }

    AppScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            if (state.selectedUids == null) {
                AppTopAppBar(
                    title = { Text(text = stringResource(R.string.autocompletes_header_autocompletes)) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                val event = AutocompletesEvent.OnSelectDrawerScreen(display = true)
                                viewModel.onEvent(event)
                            }
                        ) {
                            DefaultIcon(
                                icon = UiIcon.NavigationMenu,
                                contentDescription = UiString.FromResources(R.string.autocompletes_contentDescription_navigationMenuIcon)
                            )
                        }
                    }
                )
            } else {
                AppTopAppBar(
                    title = { Text(text = state.selectedUids?.size.toString()) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                val event = AutocompletesEvent.OnAllAutocompletesSelected(selected = false)
                                viewModel.onEvent(event)
                            }
                        ) {
                            DefaultIcon(
                                icon = UiIcon.Cancel,
                                contentDescription = UiString.FromResources(R.string.autocompletes_contentDescription_cancelSelection)
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                val event = AutocompletesEvent.OnClickClearAutocompletes
                                viewModel.onEvent(event)
                            }
                        ) {
                            DefaultIcon(
                                icon = UiIcon.ClearAutocompletes,
                                contentDescription = UiString.FromResources(R.string.autocompletes_contentDescription_clearAutocompletesIcon)
                            )
                        }

                        IconButton(
                            onClick = {
                                val event = AutocompletesEvent.OnClickDeleteAutocompletes
                                viewModel.onEvent(event)
                            }
                        ) {
                            DefaultIcon(
                                icon = UiIcon.Delete,
                                contentDescription = UiString.FromResources(R.string.autocompletes_contentDescription_deleteDataIcon)
                            )
                        }

                        IconButton(
                            onClick = {
                                val event = AutocompletesEvent.OnAllAutocompletesSelected(selected = true)
                                viewModel.onEvent(event)
                            }
                        ) {
                            DefaultIcon(
                                icon = UiIcon.SelectAll,
                                contentDescription = UiString.FromResources(R.string.autocompletes_contentDescription_selectAllDataIcon)
                            )
                        }
                    }
                )
            }
        },
        drawerContent = {
            AppDrawerContent(
                selected = DrawerScreen.AUTOCOMPLETES.toUiRoute(),
                onItemClick = {
                    val event = AutocompletesEvent.OnDrawerScreenSelected(it.toDrawerScreen())
                    viewModel.onEvent(event)
                }
            )
        },
        floatingActionButton = {
            if (state.selectedUids == null) {
                FloatingActionButton(
                    onClick = {
                        val event = AutocompletesEvent.OnClickAddAutocomplete
                        viewModel.onEvent(event)
                    }
                ) {
                    DefaultIcon(
                        icon = UiIcon.Add,
                        contentDescription = UiString.FromResources(R.string.autocompletes_contentDescription_addAutocompleteIcon),
                        tint = MaterialTheme.colors.onSecondary
                    )
                }
            }
        }
    ) { paddings ->
        AutocompletesGrid(
            modifier = Modifier.padding(paddings),
            multiColumns = state.multiColumns,
            deviceSize = state.deviceSize,
            autocompletes = state.autocompletes,
            isWaiting = state.waiting,
            notFound = {
                Text(
                    text = stringResource(R.string.autocompletes_text_autocompletesNotFound),
                    textAlign = TextAlign.Center
                )
            },
            isNotFound = state.isNotFound(),
            onClick = {
                state.selectedUids?.let { uids ->
                    val event = AutocompletesEvent.OnAutocompleteSelected(
                        selected = !uids.contains(it),
                        uid = it
                    )
                    viewModel.onEvent(event)
                }
            },
            onLongClick = {
                if (state.selectedUids == null) {
                    val event = AutocompletesEvent.OnAutocompleteSelected(
                        selected = true,
                        uid = it
                    )
                    viewModel.onEvent(event)
                }
            },
            selectedUids = state.selectedUids
        )
    }
}

@Composable
private fun AutocompletesGrid(
    modifier: Modifier = Modifier,
    multiColumns: Boolean,
    deviceSize: DeviceSize,
    autocompletes: List<AutocompleteItem>,
    isWaiting: Boolean,
    notFound: @Composable (ColumnScope.() -> Unit)? = null,
    isNotFound: Boolean,
    dropdownMenu: @Composable ((UID) -> Unit)? = null,
    onClick: (UID) -> Unit,
    onLongClick: (UID) -> Unit,
    selectedUids: List<UID>?
) {
    SmartphoneTabletAppGrid(
        modifier = modifier,
        multiColumns = multiColumns,
        multiColumnsSpace = true,
        deviceSize = deviceSize,
        isWaiting = isWaiting,
        notFound = notFound,
        isNotFound = isNotFound
    ) {
        items(autocompletes) {
            val selected = selectedUids?.contains(it.uid) ?: false

            AppSurfaceItem(
                title = getAutocompleteItemTitleOrNull(it.name),
                body = getAutocompleteItemBodyOrNull(it),
                right = getAutocompleteItemRightOrNull(selected),
                dropdownMenu = { dropdownMenu?.let { menu -> menu(it.uid) } },
                onClick = { onClick(it.uid) },
                onLongClick = { onLongClick(it.uid) },
                backgroundColor = getAppItemBackgroundColor(selected)
            )
        }
    }
}

@Composable
private fun getAutocompleteItemTitleOrNull(
    name: UiString
) = itemOrNull(enabled = name.asCompose().isNotEmpty()) {
    Text(
        modifier = Modifier.padding(AutocompleteItemTextPaddings),
        text = name.asCompose()
    )
}

@Composable
private fun getAutocompleteItemBodyOrNull(
    autocompleteItems: AutocompleteItem
) = itemOrNull(enabled = true) {
    Column {
        if (autocompleteItems.isNotFound()) {
            Text(text = stringResource(R.string.autocompletes_body_dataNotFound))
        } else {
            if (autocompleteItems.brands.isNotEmpty()) {
                Text(text = autocompleteItems.brands.asCompose())
            }
            if (autocompleteItems.sizes.isNotEmpty()) {
                Text(text = autocompleteItems.sizes.asCompose())
            }
            if (autocompleteItems.colors.isNotEmpty()) {
                Text(text = autocompleteItems.colors.asCompose())
            }
            if (autocompleteItems.manufacturers.isNotEmpty()) {
                Text(text = autocompleteItems.manufacturers.asCompose())
            }
            if (autocompleteItems.quantities.isNotEmpty()) {
                Text(text = autocompleteItems.quantities.asCompose())
            }
            if (autocompleteItems.prices.isNotEmpty()) {
                Text(text = autocompleteItems.prices.asCompose())
            }
            if (autocompleteItems.discounts.isNotEmpty()) {
                Text(text = autocompleteItems.discounts.asCompose())
            }
            if (autocompleteItems.totals.isNotEmpty()) {
                Text(text = autocompleteItems.totals.asCompose())
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