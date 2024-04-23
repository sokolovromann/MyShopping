package ru.sokolovromann.myshopping.ui.compose

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
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
import ru.sokolovromann.myshopping.data.model.DeviceSize
import ru.sokolovromann.myshopping.data.model.NightTheme
import ru.sokolovromann.myshopping.ui.DrawerScreen
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.chooseNavigate
import ru.sokolovromann.myshopping.ui.compose.event.SettingsScreenEvent
import ru.sokolovromann.myshopping.ui.model.SettingItem
import ru.sokolovromann.myshopping.ui.model.SettingUid
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
    val subjectText = stringResource(R.string.data_email_subject)
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

                is SettingsScreenEvent.OnSendEmailToDeveloper -> navController.chooseNavigate(
                    intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(it.email))
                        putExtra(Intent.EXTRA_SUBJECT, subjectText)
                    }
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

                is SettingsScreenEvent.OnShowAppGithub -> navController.chooseNavigate(
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(it.link)
                    )
                )

                is SettingsScreenEvent.OnShowPrivacyPolicy -> navController.chooseNavigate(
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(it.link)
                    )
                )

                is SettingsScreenEvent.OnShowTermsAndConditions -> navController.chooseNavigate(
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(it.link)
                    )
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
                        },
                        content = {
                            NavigationMenuIcon(
                                contentDescription = UiString.FromResources(R.string.settings_contentDescription_navigationMenuIcon)
                            )
                        }
                    )
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

@OptIn(ExperimentalFoundationApi::class)
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