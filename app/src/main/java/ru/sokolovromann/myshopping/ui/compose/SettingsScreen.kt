package ru.sokolovromann.myshopping.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.model.AfterAddShopping
import ru.sokolovromann.myshopping.data.model.AfterProductCompleted
import ru.sokolovromann.myshopping.data.model.AfterSaveProduct
import ru.sokolovromann.myshopping.data.model.AfterShoppingCompleted
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.NightTheme
import ru.sokolovromann.myshopping.ui.DrawerScreen
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.compose.event.SettingsScreenEvent
import ru.sokolovromann.myshopping.ui.model.SettingItem
import ru.sokolovromann.myshopping.ui.model.SettingUid
import ru.sokolovromann.myshopping.ui.model.UiIcon
import ru.sokolovromann.myshopping.ui.model.UiString
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.utils.updateProductsWidgets
import ru.sokolovromann.myshopping.ui.viewmodel.SettingsViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.SettingsEvent

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state = viewModel.settingsState
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                SettingsScreenEvent.OnShowBackScreen -> {
                    navController.popBackStack()
                }

                SettingsScreenEvent.OnEditCurrencyScreen -> navController.navigate(
                    route = UiRoute.Settings.editCurrencySymbolScreen
                )

                SettingsScreenEvent.OnEditTaxRateScreen -> navController.navigate(
                    route = UiRoute.Settings.editTaxRateScreen
                )

                SettingsScreenEvent.OnShowMaxAutocompletes -> navController.navigate(
                    route = UiRoute.Settings.maxAutocompletesScreen
                )

                SettingsScreenEvent.OnShowSwipeProduct -> navController.navigate(
                    route = UiRoute.Settings.swipeProduct
                )

                SettingsScreenEvent.OnShowSwipeShopping -> navController.navigate(
                    route = UiRoute.Settings.swipeShopping
                )

                SettingsScreenEvent.OnShowBackupScreen -> navController.navigate(
                    route = UiRoute.Settings.backupScreen
                )

                SettingsScreenEvent.OnShowFontSizes -> navController.navigate(
                    route = UiRoute.Settings.fontSizesScreen
                )

                SettingsScreenEvent.OnShowDisplayCompleted -> navController.navigate(
                    route = UiRoute.Settings.displayCompletedScreen
                )

                SettingsScreenEvent.OnUpdateProductsWidgets -> {
                    updateProductsWidgets(context)
                }

                is SettingsScreenEvent.OnDrawerScreenSelected -> {
                    navController.navigateWithDrawerOption(route = it.drawerScreen.getScreen())
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                is SettingsScreenEvent.OnSelectDrawerScreen -> {
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
        viewModel.onEvent(SettingsEvent.OnSelectDrawerScreen(false))
    }

    AppScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppTopAppBar(
                title = { Text(text = stringResource(R.string.settings_header_settings)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            val event = SettingsEvent.OnSelectDrawerScreen(display = true)
                            viewModel.onEvent(event)
                        }
                    ) {
                        DefaultIcon(
                            icon = UiIcon.NavigationMenu,
                            contentDescription = UiString.FromResources(R.string.settings_contentDescription_navigationMenuIcon)
                        )
                    }
                }
            )
        },
        drawerContent = {
            AppDrawerContent(
                selected = DrawerScreen.SETTINGS.toUiRoute(),
                onItemClick = {
                    val event = SettingsEvent.OnDrawerScreenSelected(it.toDrawerScreen())
                    viewModel.onEvent(event)
                }
            )
        }
    ) { paddings ->
        SettingsGrid(
            modifier = Modifier.padding(paddings),
            multiColumns = state.multiColumns,
            deviceSize = state.deviceSize,
            map = state.settings,
            isWaiting = state.waiting,
            dropdownMenu = {
                when (it) {
                    SettingUid.NightTheme -> {
                        SettingsNightThemeMenu(
                            expanded = it == state.selectedUid,
                            nightTheme = state.nightTheme.selected,
                            onDismissRequest = {
                                val event = SettingsEvent.OnSelectSettingItem(false, SettingUid.NightTheme)
                                viewModel.onEvent(event)
                            },
                            onSelected = { nightTheme ->
                                val event = SettingsEvent.OnNightThemeSelected(nightTheme)
                                viewModel.onEvent(event)
                            }
                        )
                    }

                    SettingUid.AfterProductCompleted -> {
                        SettingsAfterProductCompletedMenu(
                            expanded = it == state.selectedUid,
                            afterProductCompleted = state.afterProductCompleted.selected,
                            onDismissRequest = {
                                val event = SettingsEvent.OnSelectSettingItem(
                                    expanded = false,
                                    uid = SettingUid.AfterProductCompleted
                                )
                                viewModel.onEvent(event)
                            },
                            onSelected = { afterProductCompleted ->
                                val event = SettingsEvent.OnAfterProductCompletedSelected(afterProductCompleted)
                                viewModel.onEvent(event)
                            }
                        )
                    }

                    SettingUid.AfterSaveProduct -> {
                        SettingsAfterSaveProductMenu(
                            expanded = it == state.selectedUid,
                            afterSaveProduct = state.afterSaveProduct.selected,
                            onDismissRequest = {
                                val event = SettingsEvent.OnSelectSettingItem(
                                    expanded = false,
                                    uid = SettingUid.AfterSaveProduct
                                )
                                viewModel.onEvent(event)
                            },
                            onSelected = { afterSaveProduct ->
                                val event = SettingsEvent.OnAfterSaveProductSelected(afterSaveProduct)
                                viewModel.onEvent(event)
                            }
                        )
                    }

                    SettingUid.AfterAddShopping -> {
                        SettingsAfterAddShoppingMenu(
                            expanded = it == state.selectedUid,
                            afterAddShopping = state.afterAddShopping.selected,
                            onDismissRequest = {
                                val event = SettingsEvent.OnSelectSettingItem(
                                    expanded = false,
                                    uid = SettingUid.AfterAddShopping
                                )
                                viewModel.onEvent(event)
                            },
                            onSelected = { afterAddShopping ->
                                val event = SettingsEvent.OnAfterAddShoppingSelected(afterAddShopping)
                                viewModel.onEvent(event)
                            }
                        )
                    }

                    SettingUid.AfterShoppingCompleted -> {
                        SettingsAfterShoppingCompletedMenu(
                            expanded = it == state.selectedUid,
                            afterShoppingCompleted = state.afterShoppingCompleted.selected,
                            onDismissRequest = {
                                val event = SettingsEvent.OnSelectSettingItem(
                                    expanded = false,
                                    uid = SettingUid.AfterShoppingCompleted
                                )
                                viewModel.onEvent(event)
                            },
                            onSelected = { afterShoppingCompleted ->
                                val event = SettingsEvent.OnAfterShoppingCompletedSelected(afterShoppingCompleted)
                                viewModel.onEvent(event)
                            }
                        )
                    }

                    else -> {}
                }
            },
            onClick = {
                val event = SettingsEvent.OnSettingItemSelected(it)
                viewModel.onEvent(event)
            }
        )
    }
}

@Composable
private fun SettingsGrid(
    modifier: Modifier = Modifier,
    multiColumns: Boolean,
    deviceSize: DeviceSize,
    map: Map<UiString, List<SettingItem>>,
    isWaiting: Boolean,
    dropdownMenu: @Composable (SettingUid) -> Unit,
    onClick: (SettingUid) -> Unit
) {
    SmartphoneTabletAppGrid(
        modifier = modifier,
        multiColumns = multiColumns,
        multiColumnsSpace = true,
        deviceSize = deviceSize,
        isWaiting = isWaiting,
        isNotFound = false
    ) {
        val headers = map.keys.toList()
        itemsIndexed(map.values.toList()) { index, settingsItems ->
            SettingsSurface(
                header = {
                    Text(
                        modifier = Modifier.padding(SettingsSurfaceHeaderPaddings),
                        text = headers[index].asCompose()
                    )
                },
                items = {
                    settingsItems.forEach { item ->
                        AppItem(
                            title = getSettingsItemTitle(item.title),
                            body = getSettingsItemBodyOrNull(item.body),
                            right = getSettingsItemAfterOrNull(item.checked),
                            dropdownMenu = { dropdownMenu(item.uid) },
                            onClick = { onClick(item.uid) }
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsSurface(
    header: @Composable () -> Unit,
    items: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = SettingsSurfaceMinHeight)
            .padding(SettingsSurfacePaddings),
        shape = MaterialTheme.shapes.medium,
        elevation = SettingsSurfaceElevation
    ) {
        Column(modifier = Modifier
            .background(color = MaterialTheme.colors.surface)
        ) {
            ProvideTextStyle(
                value = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.Medium
                ),
                content = { header() }
            )
            items()
        }
    }
}

@Composable
private fun SettingsNightThemeMenu(
    expanded: Boolean,
    nightTheme: NightTheme,
    onDismissRequest: () -> Unit,
    onSelected: (NightTheme) -> Unit
) {
    AppDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        AppDropdownMenuItem(
            onClick = { onSelected(NightTheme.DISABLED) },
            text = { Text(text = stringResource(R.string.settings_action_selectDisabledNightTheme)) },
            right = { CheckmarkAppCheckbox(checked = nightTheme == NightTheme.DISABLED) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(NightTheme.APP) },
            text = { Text(text = stringResource(R.string.settings_action_selectAppNightTheme)) },
            right = { CheckmarkAppCheckbox(checked = nightTheme == NightTheme.APP) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(NightTheme.WIDGET) },
            text = { Text(text = stringResource(R.string.settings_action_selectWidgetNightTheme)) },
            right = { CheckmarkAppCheckbox(checked = nightTheme == NightTheme.WIDGET) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(NightTheme.APP_AND_WIDGET) },
            text = { Text(text = stringResource(R.string.settings_action_selectAppAndWidgetNightTheme)) },
            right = { CheckmarkAppCheckbox(checked = nightTheme == NightTheme.APP_AND_WIDGET) }
        )
    }
}

@Composable
private fun SettingsAfterSaveProductMenu(
    expanded: Boolean,
    afterSaveProduct: AfterSaveProduct,
    onDismissRequest: () -> Unit,
    onSelected: (AfterSaveProduct) -> Unit
) {
    AppDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        AppDropdownMenuItem(
            onClick = { onSelected(AfterSaveProduct.NOTHING) },
            text = { Text(text = stringResource(R.string.settings_action_nothingAfterSaveProduct)) },
            right = { CheckmarkAppCheckbox(checked = afterSaveProduct == AfterSaveProduct.NOTHING) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(AfterSaveProduct.CLOSE_SCREEN) },
            text = { Text(text = stringResource(R.string.settings_action_closeAfterSaveProduct)) },
            right = { CheckmarkAppCheckbox(checked = afterSaveProduct == AfterSaveProduct.CLOSE_SCREEN) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(AfterSaveProduct.OPEN_NEW_SCREEN) },
            text = { Text(text = stringResource(R.string.settings_action_openAfterSaveProduct)) },
            right = { CheckmarkAppCheckbox(checked = afterSaveProduct == AfterSaveProduct.OPEN_NEW_SCREEN) }
        )
    }
}

@Composable
private fun SettingsAfterProductCompletedMenu(
    expanded: Boolean,
    afterProductCompleted: AfterProductCompleted,
    onDismissRequest: () -> Unit,
    onSelected: (AfterProductCompleted) -> Unit
) {
    AppDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        AppDropdownMenuItem(
            onClick = { onSelected(AfterProductCompleted.NOTHING) },
            text = { Text(text = stringResource(R.string.settings_action_nothingAfterProductCompleted)) },
            right = { CheckmarkAppCheckbox(checked = afterProductCompleted == AfterProductCompleted.NOTHING) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(AfterProductCompleted.EDIT) },
            text = { Text(text = stringResource(R.string.settings_action_editAfterProductCompleted)) },
            right = { CheckmarkAppCheckbox(checked = afterProductCompleted == AfterProductCompleted.EDIT) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(AfterProductCompleted.DELETE) },
            text = { Text(text = stringResource(R.string.settings_action_deleteAfterProductCompleted)) },
            right = { CheckmarkAppCheckbox(checked = afterProductCompleted == AfterProductCompleted.DELETE) }
        )
    }
}

@Composable
private fun SettingsAfterAddShoppingMenu(
    expanded: Boolean,
    afterAddShopping: AfterAddShopping,
    onDismissRequest: () -> Unit,
    onSelected: (AfterAddShopping) -> Unit
) {
    AppDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        AppDropdownMenuItem(
            onClick = { onSelected(AfterAddShopping.OPEN_PRODUCTS_SCREEN) },
            text = { Text(text = stringResource(R.string.settings_action_openProductsAfterAddShopping)) },
            right = { CheckmarkAppCheckbox(checked = afterAddShopping == AfterAddShopping.OPEN_PRODUCTS_SCREEN) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(AfterAddShopping.OPEN_EDIT_SHOPPING_NAME_SCREEN) },
            text = { Text(text = stringResource(R.string.settings_action_openEditNameAfterAddShopping)) },
            right = { CheckmarkAppCheckbox(checked = afterAddShopping == AfterAddShopping.OPEN_EDIT_SHOPPING_NAME_SCREEN) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(AfterAddShopping.OPEN_ADD_PRODUCT_SCREEN) },
            text = { Text(text = stringResource(R.string.settings_action_openAddProductAfterAddShopping)) },
            right = { CheckmarkAppCheckbox(checked = afterAddShopping == AfterAddShopping.OPEN_ADD_PRODUCT_SCREEN) }
        )
    }
}

@Composable
private fun SettingsAfterShoppingCompletedMenu(
    expanded: Boolean,
    afterShoppingCompleted: AfterShoppingCompleted,
    onDismissRequest: () -> Unit,
    onSelected: (AfterShoppingCompleted) -> Unit
) {
    AppDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        AppDropdownMenuItem(
            onClick = { onSelected(AfterShoppingCompleted.NOTHING) },
            text = { Text(text = stringResource(R.string.settings_action_nothingAfterShoppingCompleted)) },
            right = { CheckmarkAppCheckbox(checked = afterShoppingCompleted == AfterShoppingCompleted.NOTHING) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(AfterShoppingCompleted.ARCHIVE) },
            text = { Text(text = stringResource(R.string.settings_action_archiveAfterShoppingCompleted)) },
            right = { CheckmarkAppCheckbox(checked = afterShoppingCompleted == AfterShoppingCompleted.ARCHIVE) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(AfterShoppingCompleted.DELETE) },
            text = { Text(text = stringResource(R.string.settings_action_deleteAfterShoppingCompleted)) },
            right = { CheckmarkAppCheckbox(checked = afterShoppingCompleted == AfterShoppingCompleted.DELETE) }
        )
    }
}

@Composable
private fun getSettingsItemTitle(
    text: UiString
): @Composable () -> Unit = {
    Text(text = text.asCompose())
}

@Composable
private fun getSettingsItemBodyOrNull(
    text: UiString
) = itemOrNull(enabled = text.asCompose().isNotEmpty()) {
    Text(text = text.asCompose())
}

@Composable
private fun getSettingsItemAfterOrNull(
    checked: Boolean?
) = itemOrNull(enabled = checked != null) {
    AppSwitch(checked = checked ?: false)
}

private val SettingsSurfaceMinHeight = 56.dp
private val SettingsSurfacePaddings = PaddingValues(all = 4.dp)
private val SettingsSurfaceElevation = 1.dp
private val SettingsSurfaceHeaderPaddings = PaddingValues(
    horizontal = 8.dp,
    vertical = 16.dp
)