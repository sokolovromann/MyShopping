package ru.sokolovromann.myshopping.ui.compose

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import ru.sokolovromann.myshopping.ui.chooseNavigate
import ru.sokolovromann.myshopping.ui.compose.event.SettingsScreenEvent
import ru.sokolovromann.myshopping.ui.compose.state.*
import ru.sokolovromann.myshopping.ui.navigateWithDrawerOption
import ru.sokolovromann.myshopping.ui.viewmodel.SettingsViewModel
import ru.sokolovromann.myshopping.ui.viewmodel.event.SettingsEvent

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(Unit) {
        viewModel.screenEventFlow.collect {
            when (it) {
                SettingsScreenEvent.EditCurrency -> navController.navigate(
                    route = UiRoute.Settings.editCurrencyScreen
                )

                SettingsScreenEvent.EditTaxRate -> navController.navigate(
                    route = UiRoute.Settings.editTaxRateScreen
                )

                is SettingsScreenEvent.SendEmailToDeveloper -> navController.chooseNavigate(
                    intent = Intent(
                        Intent.ACTION_SENDTO,
                        Uri.parse("mailto:${it.email}")
                            .buildUpon()
                            .appendQueryParameter("subject", it.subject)
                            .build()
                    )
                )

                SettingsScreenEvent.ShowBackScreen -> navController.popBackStack()

                SettingsScreenEvent.ShowPurchases -> navController.navigateWithDrawerOption(
                    route = UiRoute.Purchases.purchasesScreen
                )

                SettingsScreenEvent.ShowArchive -> navController.navigateWithDrawerOption(
                    route = UiRoute.Archive.archiveScreen
                )

                SettingsScreenEvent.ShowTrash -> navController.navigateWithDrawerOption(
                    route = UiRoute.Trash.trashScreen
                )

                SettingsScreenEvent.ShowAutocompletes -> navController.navigateWithDrawerOption(
                    route = UiRoute.Autocompletes.autocompletesScreen
                )

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
            }
        }
    }

    BackHandler(enabled = scaffoldState.drawerState.isOpen) {
        viewModel.onEvent(SettingsEvent.HideNavigationDrawer)
    }

    AppSystemUi(
        systemUiController = systemUiController,
        data = viewModel.systemUiState.value
    )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(viewModel) },
        drawerContent = { DrawerContent(viewModel) },
        content = { paddingValues -> Content(paddingValues, viewModel) }
    )
}

@Composable
private fun TopBar(viewModel: SettingsViewModel) {
    AppTopBar(
        data = viewModel.topBarState.value,
        onNavigationIconClick = { viewModel.onEvent(SettingsEvent.ShowNavigationDrawer) }
    )
}

@Composable
private fun DrawerContent(viewModel: SettingsViewModel) {
    AppNavigationDrawer(
        data = viewModel.navigationDrawerState.value,
        onClick = {
            val event = SettingsEvent.SelectNavigationItem(it.route)
            viewModel.onEvent(event)
        }
    )
}

@Composable
private fun Content(paddingValues: PaddingValues, viewModel: SettingsViewModel) {
    Box(modifier = Modifier.padding(paddingValues)) {
        val mapData = viewModel.settingsState.currentData
        when (mapData.result) {
            MapResult.Showing -> SettingsShowing(mapData, viewModel)
            MapResult.Loading -> SettingsLoading()
            MapResult.NotFound, MapResult.Nothing -> {}
        }
    }
}

@Composable
private fun SettingsShowing(
    data: MapData<TextData, List<SettingsItem>>,
    viewModel: SettingsViewModel
) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(4.dp)
        .verticalScroll(scrollState)
    ) {
        AppGrid(data = data) {
            data.items.forEach { SettingsItems(it.key, it.value, viewModel) }
        }
        Spacer(modifier = Modifier.height(128.dp))
    }
}

@Composable
private fun SettingsLoading() {
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
private fun SettingsItems(
    header: TextData,
    items: List<SettingsItem>,
    viewModel: SettingsViewModel
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .padding(all = 4.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = 1.dp
    ) {
        Column {
            AppText(
                modifier = Modifier.padding(8.dp),
                data = header
            )

            items.forEach {
                AppItem(
                    title = itemTitle(it, viewModel),
                    body = itemBodyOrNull(it),
                    after = itemAfterOrNull(it),
                    onClick = {
                        val event = SettingsEvent.SelectSettingsItem(it.uid)
                        viewModel.onEvent(event)
                    }
                )
            }
        }
    }
}

@Composable
private fun FontSizeMenu(settingsUid: SettingsUid, viewModel: SettingsViewModel) {
    val menuData = viewModel.fontSizeState.currentData
    val menu = menuData.menu ?: return

    DropdownMenu(
        expanded = menuData.itemUid == settingsUid.name,
        onDismissRequest = { viewModel.onEvent(SettingsEvent.HideFontSize) }
    ) {
        AppMenuItem(
            before = { AppRadioButton(data = menu.tinySelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.tinyBody
                )
            },
            onClick = {
                val event = SettingsEvent.TinyFontSizeSelected
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.smallSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.smallBody
                )
            },
            onClick = {
                val event = SettingsEvent.SmallFontSizeSelected
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.mediumSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.mediumBody
                )
            },
            onClick = {
                val event = SettingsEvent.MediumFontSizeSelected
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.largeSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.largeBody
                )
            },
            onClick = {
                val event = SettingsEvent.LargeFontSizeSelected
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.hugeSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.hugeBody
                )
            },
            onClick = {
                val event = SettingsEvent.HugeFontSizeSelected
                viewModel.onEvent(event)
            }
        )
    }
}

@Composable
private fun DisplayAutocompleteMenu(settingsUid: SettingsUid, viewModel: SettingsViewModel) {
    val menuData = viewModel.displayAutocompleteState.currentData
    val menu = menuData.menu ?: return

    DropdownMenu(
        expanded = menuData.itemUid == settingsUid.name,
        onDismissRequest = { viewModel.onEvent(SettingsEvent.HideProductsDisplayAutocomplete) }
    ) {
        AppMenuItem(
            before = { AppRadioButton(data = menu.allSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.allBody
                )
            },
            onClick = {
                val event = SettingsEvent.DisplayProductsAllAutocomplete
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.nameSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.nameBody
                )
            },
            onClick = {
                val event = SettingsEvent.DisplayProductsNameAutocomplete
                viewModel.onEvent(event)
            }
        )
        AppMenuItem(
            before = { AppRadioButton(data = menu.hideSelected)},
            text = {
                AppText(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    data = menu.hideBody
                )
            },
            onClick = {
                val event = SettingsEvent.HideProductsAutocomplete
                viewModel.onEvent(event)
            }
        )
    }
}

@Composable
private fun itemTitle(
    item: SettingsItem,
    viewModel: SettingsViewModel
): @Composable (() -> Unit) {
    return {
        AppText(data = item.title)
        FontSizeMenu(item.uid, viewModel)
        DisplayAutocompleteMenu(item.uid, viewModel)
    }
}

@Composable
private fun itemBodyOrNull(item: SettingsItem): @Composable (() -> Unit)? {
    if (item.body.isTextHiding()) {
        return null
    }

    return {
        Column { AppText(data = item.body) }
    }
}

@Composable
private fun itemAfterOrNull(item: SettingsItem): @Composable (() -> Unit)? {
    if (item.checked == null) {
        return null
    }

    return {
        Spacer(modifier = Modifier.width(8.dp))
        AppSwitch(data = item.checked)
    }
}