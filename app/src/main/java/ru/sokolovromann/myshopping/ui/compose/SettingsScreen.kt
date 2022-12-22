package ru.sokolovromann.myshopping.ui.compose

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
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
                    route = UiRoute.Settings.editCurrencySymbolScreen
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

    AppSystemUi(systemUiController = systemUiController)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(viewModel) },
        drawerContent = { DrawerContent(viewModel) },
        content = { paddingValues -> Content(paddingValues, viewModel) }
    )
}

@Composable
private fun TopBar(viewModel: SettingsViewModel) {
    TopAppBar(
        title = { Text(text = viewModel.topBarState.value.title.text.asCompose()) },
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
        elevation = 1.dp,
    ) {
        Column(modifier = Modifier.background(color = MaterialTheme.colors.surface)) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                text = header.text.asCompose(),
                fontSize = header.fontSize,
                fontWeight = FontWeight.Medium
            )

            items.forEach {
                AppItem(
                    title = itemTitle(it),
                    body = itemBodyOrNull(it),
                    after = itemAfterOrNull(it),
                    dropdownMenu = {
                        FontSizeMenu(it.uid, viewModel)
                        DisplayAutocompleteMenu(it.uid, viewModel)
                    },
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

    AppDropdownMenu(
        expanded = menuData.itemUid == settingsUid.name,
        onDismissRequest = { viewModel.onEvent(SettingsEvent.HideFontSize) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.TinyFontSizeSelected) },
            text = { Text(text = menu.tinyBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.tinySelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.SmallFontSizeSelected) },
            text = { Text(text = menu.smallBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.smallSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.MediumFontSizeSelected) },
            text = { Text(text = menu.mediumBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.mediumSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.LargeFontSizeSelected) },
            text = { Text(text = menu.largeBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.largeSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.HugeFontSizeSelected) },
            text = { Text(text = menu.hugeBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.hugeSelected.selected) }
        )
    }
}

@Composable
private fun DisplayAutocompleteMenu(settingsUid: SettingsUid, viewModel: SettingsViewModel) {
    val menuData = viewModel.displayAutocompleteState.currentData
    val menu = menuData.menu ?: return

    AppDropdownMenu(
        expanded = menuData.itemUid == settingsUid.name,
        onDismissRequest = { viewModel.onEvent(SettingsEvent.HideProductsDisplayAutocomplete) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.DisplayProductsAllAutocomplete) },
            text = { Text(text = menu.allBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.allSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.DisplayProductsNameAutocomplete) },
            text = { Text(text = menu.nameBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.nameSelected.selected) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.HideProductsAutocomplete) },
            text = { Text(text = menu.hideBody.text.asCompose()) },
            after = { CheckmarkAppCheckbox(checked = menu.hideSelected.selected) }
        )
    }
}

@Composable
private fun itemTitle(item: SettingsItem): @Composable (() -> Unit) = {
    val title = item.title
    Text(
        text = title.text.asCompose(),
        fontSize = title.fontSize
    )
}

@Composable
private fun itemBodyOrNull(item: SettingsItem): @Composable (() -> Unit)? {
    val body = item.body
    return itemOrNull(enabled = body.isTextShowing()) {
        Text(
            text = body.text.asCompose(),
            fontSize = body.fontSize
        )
    }
}

@Composable
private fun itemAfterOrNull(item: SettingsItem): @Composable (() -> Unit)? {
    val checked = item.checked
    return itemOrNull(enabled = checked != null) {
        AppSwitch(checked = checked?.checked ?: false)
    }
}