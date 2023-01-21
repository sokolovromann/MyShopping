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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.R
import ru.sokolovromann.myshopping.data.repository.model.DisplayAutocomplete
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
    AppDrawerContent(
        selected = UiRoute.Settings,
        onItemClick = {
            val event = SettingsEvent.SelectNavigationItem(it)
            viewModel.onEvent(event)
        }
    )
}

@Composable
private fun Content(paddingValues: PaddingValues, viewModel: SettingsViewModel) {
    Box(modifier = Modifier.padding(paddingValues)) {
        val screenData = viewModel.settingsState.screenData
        when (screenData.screenState) {
            ScreenState.Nothing -> {}
            ScreenState.Loading -> SettingsLoading()
            ScreenState.Showing -> SettingsShowing(viewModel)
            ScreenState.Saving -> {}
        }
    }
}

@Composable
private fun SettingsShowing(viewModel: SettingsViewModel) {
    val screenData = viewModel.settingsState.screenData
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(4.dp)
        .verticalScroll(scrollState)
    ) {
        AppGrid(multiColumns = screenData.multiColumns) {
            screenData.settings.forEach { SettingsItems(it.key, it.value, viewModel) }
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
    header: UiText,
    items: List<SettingsItem>,
    viewModel: SettingsViewModel
) {
    val fontSize = viewModel.settingsState.screenData.fontSize
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
                text = header.asCompose(),
                fontSize = fontSize.toItemTitle().sp,
                fontWeight = FontWeight.Medium
            )

            items.forEach {
                AppItem(
                    title = itemTitle(it, fontSize),
                    body = itemBodyOrNull(it, fontSize),
                    after = itemAfterOrNull(it),
                    dropdownMenu = {
                        when (it.uid) {
                            SettingsUid.FontSize -> FontSizeMenu(it.uid, viewModel)
                            SettingsUid.DisplayAutocomplete -> DisplayAutocompleteMenu(it.uid, viewModel)
                            else -> {}
                        }
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
    val screenData = viewModel.settingsState.screenData
    val fontSize = screenData.fontSize

    AppDropdownMenu(
        expanded = settingsUid == screenData.settingsItemUid,
        onDismissRequest = { viewModel.onEvent(SettingsEvent.HideFontSize) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.TinyFontSizeSelected) },
            text = { Text(text = stringResource(R.string.settings_action_selectTinyFontSize)) },
            after = { CheckmarkAppCheckbox(checked = fontSize == FontSize.TINY) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.SmallFontSizeSelected) },
            text = { Text(text = stringResource(R.string.settings_action_selectSmallFontSize)) },
            after = { CheckmarkAppCheckbox(checked = fontSize == FontSize.SMALL) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.MediumFontSizeSelected) },
            text = { Text(text = stringResource(R.string.settings_action_selectMediumFontSize)) },
            after = { CheckmarkAppCheckbox(checked = fontSize == FontSize.MEDIUM) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.LargeFontSizeSelected) },
            text = { Text(text = stringResource(R.string.settings_action_selectLargeFontSize)) },
            after = { CheckmarkAppCheckbox(checked = fontSize == FontSize.LARGE) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.HugeFontSizeSelected) },
            text = { Text(text = stringResource(R.string.settings_action_selectHugeFontSize)) },
            after = { CheckmarkAppCheckbox(checked = fontSize == FontSize.HUGE) }
        )
    }
}

@Composable
private fun DisplayAutocompleteMenu(settingsUid: SettingsUid, viewModel: SettingsViewModel) {
    val screenData = viewModel.settingsState.screenData
    val displayAutocomplete = screenData.displayAutocomplete

    AppDropdownMenu(
        expanded = settingsUid == screenData.settingsItemUid,
        onDismissRequest = { viewModel.onEvent(SettingsEvent.HideProductsDisplayAutocomplete) }
    ) {
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.DisplayProductsAllAutocomplete) },
            text = { Text(text = stringResource(R.string.settings_action_displayAllAutocomplete)) },
            after = { CheckmarkAppCheckbox(checked = displayAutocomplete == DisplayAutocomplete.ALL) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.DisplayProductsNameAutocomplete) },
            text = { Text(text = stringResource(R.string.settings_action_displayNameAutocomplete)) },
            after = { CheckmarkAppCheckbox(checked = displayAutocomplete == DisplayAutocomplete.NAME) }
        )
        AppDropdownMenuItem(
            onClick = { viewModel.onEvent(SettingsEvent.HideProductsAutocomplete) },
            text = { Text(text = stringResource(R.string.settings_action_selectHideAutocomplete)) },
            after = { CheckmarkAppCheckbox(checked = displayAutocomplete == DisplayAutocomplete.HIDE) }
        )
    }
}

@Composable
private fun itemTitle(item: SettingsItem, fontSize: FontSize): @Composable (() -> Unit) = {
    val title = item.titleText
    Text(
        text = title.asCompose(),
        fontSize = fontSize.toItemTitle().sp
    )
}

@Composable
private fun itemBodyOrNull(item: SettingsItem, fontSize: FontSize): @Composable (() -> Unit)? {
    val body = item.bodyText
    return itemOrNull(enabled = body != UiText.Nothing) {
        Text(
            text = body.asCompose(),
            fontSize = fontSize.toItemBody().sp
        )
    }
}

@Composable
private fun itemAfterOrNull(item: SettingsItem): @Composable (() -> Unit)? {
    val checked = item.checked
    return itemOrNull(enabled = checked != null) {
        AppSwitch(checked = checked ?: false)
    }
}