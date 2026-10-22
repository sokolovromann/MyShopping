package ru.sokolovromann.myshopping.feature.settings.component

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.core.ui.component.NavigationDrawer
import ru.sokolovromann.myshopping.core.ui.component.SimpleScaffold
import ru.sokolovromann.myshopping.core.ui.component.isCloseableNavigationDrawer
import ru.sokolovromann.myshopping.core.ui.component.rememberDefaultDrawerState
import ru.sokolovromann.myshopping.core.ui.model.NavigationItem
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.SettingsViewModel
import ru.sokolovromann.myshopping.feature.settings.action.AddEditProductSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.BackupSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.CartsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.GeneralSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.ProductsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.ProductsWidgetSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.SuggestionsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.event.SettingsEvent
import ru.sokolovromann.myshopping.feature.settings.model.SettingsState

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel = hiltViewModel()) {
    val settingsState by settingsViewModel.settingsState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    SettingsScreenImpl(
        onNavigationItemSelected = { settingsViewModel.onNavigationItemSelected(it) },
        onBackClick = { settingsViewModel.onBackClick() },
        onGeneralSettingsAction = { settingsViewModel.onGeneralSettingsActions(it) },
        onCartsSettingsAction = { settingsViewModel.onCartsSettingsAction(it) },
        onProductsSettingsAction = { settingsViewModel.onProductsSettingsAction(it) },
        onProductsWidgetSettingsAction = { settingsViewModel.onProductsWidgetSettingsAction(it) },
        onAddEditProductSettingsAction = { settingsViewModel.onAddEditProductSettingsAction(it) },
        onSuggestionsSettingsAction = { settingsViewModel.onSuggestionsSettingsAction(it) },
        onBackupSettingsAction = { settingsViewModel.onBackupSettingsAction(it) },
        snackbarHostState = snackbarHostState,
        settingsState = settingsState
    )

    val successExportMessage = stringResource(R.string.settings_text_backup_export_success)
    val successImportMessage = stringResource(R.string.settings_text_backup_import_success)
    val failureMessage = stringResource(R.string.settings_text_backup_failure)
    LaunchedEffect(key1 = true) {
        settingsViewModel.settingsEvent.collect { event ->
            when (event) {
                SettingsEvent.ShowExportBackupSuccessMessage ->
                    snackbarHostState.showSnackbar(
                        message = successExportMessage
                    )
                SettingsEvent.ShowImportBackupSuccessMessage ->
                    snackbarHostState.showSnackbar(
                        message = successImportMessage
                    )
                SettingsEvent.ShowBackupFailureMessage ->
                    snackbarHostState.showSnackbar(
                        message = failureMessage,
                        duration = SnackbarDuration.Long
                    )
            }
        }
    }
}

@Composable
private fun SettingsScreenImpl(
    onNavigationItemSelected: (NavigationItem) -> Unit,
    onBackClick: () -> Unit,
    onGeneralSettingsAction: (GeneralSettingsAction) -> Unit,
    onCartsSettingsAction: (CartsSettingsAction) -> Unit,
    onProductsSettingsAction: (ProductsSettingsAction) -> Unit,
    onProductsWidgetSettingsAction: (ProductsWidgetSettingsAction) -> Unit,
    onAddEditProductSettingsAction: (AddEditProductSettingsAction) -> Unit,
    onSuggestionsSettingsAction: (SuggestionsSettingsAction) -> Unit,
    onBackupSettingsAction: (BackupSettingsAction) -> Unit,
    snackbarHostState: SnackbarHostState,
    settingsState: SettingsState
) {
    val drawerState = rememberDefaultDrawerState()
    val isCloseableNavigationDrawer = isCloseableNavigationDrawer()
    val scope = rememberCoroutineScope()
    fun openDrawer() = scope.launch { drawerState.open() }
    fun closeDrawer() = scope.launch { drawerState.close() }

    NavigationDrawer(
        drawerState = drawerState,
        initialItem = NavigationItem.Settings,
        onItemClick = {
            if (isCloseableNavigationDrawer) closeDrawer()
            onNavigationItemSelected(it)
        }
    ) {
        SimpleScaffold(
            topBar = { SettingsNavigationTopAppBar { openDrawer() } },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            onBackClick = onBackClick,
            content = {
                SettingsContent(
                    onGeneralSettingsAction = onGeneralSettingsAction,
                    onCartsSettingsAction = onCartsSettingsAction,
                    onProductsSettingsAction = onProductsSettingsAction,
                    onProductsWidgetSettingsAction = onProductsWidgetSettingsAction,
                    onAddEditProductSettingsAction = onAddEditProductSettingsAction,
                    onSuggestionsSettingsAction = onSuggestionsSettingsAction,
                    onBackupSettingsAction = onBackupSettingsAction,
                    settingsState = settingsState
                )
            }
        )
    }
}