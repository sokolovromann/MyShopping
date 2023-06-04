package ru.sokolovromann.myshopping.ui.compose

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.DisplayCompleted
import ru.sokolovromann.myshopping.data.repository.model.DisplayProducts
import ru.sokolovromann.myshopping.data.repository.model.FontSize
import ru.sokolovromann.myshopping.ui.UiRoute
import ru.sokolovromann.myshopping.ui.chooseNavigate
import ru.sokolovromann.myshopping.ui.compose.event.SettingsScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.utils.toItemBody
import ru.sokolovromann.myshopping.ui.utils.toItemTitle
import ru.sokolovromann.myshopping.ui.viewmodel.SettingsViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.SettingsEvent

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val screenData = viewModel.settingsState.screenData
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val subjectText = stringResource(R.string.data_email_subject)

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                SettingsScreenEvent.EditCurrency -> navController.navigate(
                    route = UiRoute.Settings.editCurrencySymbolScreen
                )

                SettingsScreenEvent.EditTaxRate -> navController.navigate(
                    route = UiRoute.Settings.editTaxRateScreen
                )

                is SettingsScreenEvent.SendEmailToDeveloper -> navController.chooseNavigate(
                    intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(it.email))
                        putExtra(Intent.EXTRA_SUBJECT, subjectText)
                    }
                )

                SettingsScreenEvent.ShowBackScreen -> navController.popBackStack()

                SettingsScreenEvent.ShowPurchases -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Purchases.purchasesScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                SettingsScreenEvent.ShowArchive -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Archive.archiveScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                SettingsScreenEvent.ShowTrash -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Trash.trashScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                SettingsScreenEvent.ShowAutocompletes -> {
                    navController.navigateWithDrawerOption(route = UiRoute.Autocompletes.autocompletesScreen)
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                }

                SettingsScreenEvent.ShowNavigationDrawer -> coroutineScope.launch {
                    scaffoldState.drawerState.open()
                }

                is SettingsScreenEvent.ShowAppGithub -> navController.chooseNavigate(
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(it.link)
                    )
                )

                SettingsScreenEvent.HideNavigationDrawer -> coroutineScope.launch {
                    scaffoldState.drawerState.close()
                }

                is SettingsScreenEvent.ShowPrivacyPolicy -> navController.chooseNavigate(
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(it.link)
                    )
                )

                is SettingsScreenEvent.ShowTermsAndConditions -> navController.chooseNavigate(
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(it.link)
                    )
                )
            }
        }
    }

    BackHandler(enabled = scaffoldState.drawerState.isOpen) {
        viewModel.onEvent(SettingsEvent.HideNavigationDrawer)
    }

    AppScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings_header_settings)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(SettingsEvent.ShowNavigationDrawer) }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(R.string.settings_contentDescription_navigationIcon),
                            tint = contentColorFor(MaterialTheme.colors.primarySurface).copy(ContentAlpha.medium)
                        )
                    }
                }
            )
        },
        drawerContent = {
            AppDrawerContent(
                selected = UiRoute.Settings,
                onItemClick = {
                    val event = SettingsEvent.SelectNavigationItem(it)
                    viewModel.onEvent(event)
                }
            )
        }
    ) { paddings ->
        SettingsGrid(
            modifier = Modifier.padding(paddings),
            screenState = screenData.screenState,
            multiColumns = screenData.multiColumns,
            smartphoneScreen = screenData.smartphoneScreen,
            map = screenData.settings,
            fontSize = screenData.fontSize,
            dropdownMenu = {
                when (it) {
                    SettingsUid.FontSize -> {
                        SettingsFontSizeMenu(
                            expanded = it == screenData.settingsItemUid,
                            fontSize = screenData.fontSize,
                            onDismissRequest = { viewModel.onEvent(SettingsEvent.HideFontSize) },
                            onSelected = { fontSize ->
                                viewModel.onEvent(SettingsEvent.FontSizeSelected(fontSize))
                            }
                        )
                    }

                    SettingsUid.DisplayCompletedPurchases -> {
                        SettingsDisplayCompletedMenu(
                            expanded = it == screenData.settingsItemUid,
                            displayCompleted = screenData.displayCompletedPurchases,
                            onDismissRequest = { viewModel.onEvent(SettingsEvent.HideDisplayCompletedPurchases) },
                            onSelected = { displayCompleted ->
                                viewModel.onEvent(SettingsEvent.DisplayCompletedPurchasesSelected(displayCompleted))
                            }
                        )
                    }

                    SettingsUid.DisplayShoppingsProducts -> {
                        SettingsDisplayShoppingsProductsMenu(
                            expanded = it == screenData.settingsItemUid,
                            displayProducts = screenData.displayShoppingsProducts,
                            onDismissRequest = { viewModel.onEvent(SettingsEvent.HideDisplayShoppingsProducts) },
                            onSelected = { displayProducts ->
                                viewModel.onEvent(SettingsEvent.DisplayShoppingsProductsSelected(displayProducts))
                            }
                        )
                    }

                    else -> {}
                }
            },
            onClick = {
                val event = SettingsEvent.SelectSettingsItem(it)
                viewModel.onEvent(event)
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SettingsGrid(
    modifier: Modifier = Modifier,
    screenState: ScreenState,
    multiColumns: Boolean,
    smartphoneScreen: Boolean,
    map: Map<UiText, List<SettingsItem>>,
    fontSize: FontSize,
    dropdownMenu: @Composable (SettingsUid) -> Unit,
    onClick: (SettingsUid) -> Unit
) {
    SmartphoneTabletAppGrid(
        modifier = modifier,
        screenState = screenState,
        multiColumns = multiColumns,
        multiColumnsSpace = true,
        smartphoneScreen = smartphoneScreen
    ) {
        val headers = map.keys.toList()
        itemsIndexed(map.values.toList()) { index, settingsItems ->
            SettingsSurface(
                header = {
                    Text(
                        modifier = Modifier.padding(SettingsSurfaceHeaderPaddings),
                        text = headers[index].asCompose(),
                        fontSize = fontSize.toItemTitle().sp
                    )
                },
                items = {
                    settingsItems.forEach { item ->
                        AppItem(
                            title = getSettingsItemTitle(item.titleText, fontSize),
                            body = getSettingsItemBodyOrNull(item.bodyText, fontSize),
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
private fun SettingsFontSizeMenu(
    expanded: Boolean,
    fontSize: FontSize,
    onDismissRequest: () -> Unit,
    onSelected: (FontSize) -> Unit,
) {
    AppDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        AppDropdownMenuItem(
            onClick = { onSelected(FontSize.TINY) },
            text = { Text(text = stringResource(R.string.settings_action_selectTinyFontSize)) },
            right = { CheckmarkAppCheckbox(checked = fontSize == FontSize.TINY) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(FontSize.SMALL) },
            text = { Text(text = stringResource(R.string.settings_action_selectSmallFontSize)) },
            right = { CheckmarkAppCheckbox(checked = fontSize == FontSize.SMALL) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(FontSize.MEDIUM) },
            text = { Text(text = stringResource(R.string.settings_action_selectMediumFontSize)) },
            right = { CheckmarkAppCheckbox(checked = fontSize == FontSize.MEDIUM) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(FontSize.LARGE) },
            text = { Text(text = stringResource(R.string.settings_action_selectLargeFontSize)) },
            right = { CheckmarkAppCheckbox(checked = fontSize == FontSize.LARGE) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(FontSize.HUGE) },
            text = { Text(text = stringResource(R.string.settings_action_selectHugeFontSize)) },
            right = { CheckmarkAppCheckbox(checked = fontSize == FontSize.HUGE) }
        )
    }
}

@Composable
private fun SettingsDisplayCompletedMenu(
    expanded: Boolean,
    displayCompleted: DisplayCompleted,
    onDismissRequest: () -> Unit,
    onSelected: (DisplayCompleted) -> Unit
) {
    AppDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        AppDropdownMenuItem(
            onClick = { onSelected(DisplayCompleted.FIRST) },
            text = { Text(text = stringResource(R.string.settings_action_displayCompletedPurchasesFirst)) },
            right = { CheckmarkAppCheckbox(checked = displayCompleted == DisplayCompleted.FIRST) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(DisplayCompleted.LAST) },
            text = { Text(text = stringResource(R.string.settings_action_displayCompletedPurchasesLast)) },
            right = { CheckmarkAppCheckbox(checked = displayCompleted == DisplayCompleted.LAST) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(DisplayCompleted.HIDE) },
            text = { Text(text = stringResource(R.string.settings_action_hideCompletedPurchases)) },
            right = { CheckmarkAppCheckbox(checked = displayCompleted == DisplayCompleted.HIDE) }
        )
    }
}

@Composable
private fun SettingsDisplayShoppingsProductsMenu(
    expanded: Boolean,
    displayProducts: DisplayProducts,
    onDismissRequest: () -> Unit,
    onSelected: (DisplayProducts) -> Unit
) {
    AppDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        AppDropdownMenuItem(
            onClick = { onSelected(DisplayProducts.COLUMNS) },
            text = { Text(text = stringResource(R.string.settings_action_displayShoppingsProductsColumns)) },
            right = { CheckmarkAppCheckbox(checked = displayProducts == DisplayProducts.COLUMNS) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(DisplayProducts.ROW) },
            text = { Text(text = stringResource(R.string.settings_action_displayShoppingsProductsRow)) },
            right = { CheckmarkAppCheckbox(checked = displayProducts == DisplayProducts.ROW) }
        )
        AppDropdownMenuItem(
            onClick = { onSelected(DisplayProducts.HIDE) },
            text = { Text(text = stringResource(R.string.settings_action_hideShoppingsProducts)) },
            right = { CheckmarkAppCheckbox(checked = displayProducts == DisplayProducts.HIDE) }
        )
    }
}

@Composable
private fun getSettingsItemTitle(
    text: UiText,
    fontSize: FontSize
): @Composable () -> Unit = {
    Text(
        text = text.asCompose(),
        fontSize = fontSize.toItemTitle().sp
    )
}

@Composable
private fun getSettingsItemBodyOrNull(
    text: UiText,
    fontSize: FontSize
) = itemOrNull(enabled = text.asCompose().isNotEmpty()) {
    Text(
        text = text.asCompose(),
        fontSize = fontSize.toItemBody().sp
    )
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