package ru.sokolovromann.myshopping.feature.settings.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.MainDispatcher
import ru.sokolovromann.myshopping.core.navigation.Navigator
import ru.sokolovromann.myshopping.core.navigation.Screen
import ru.sokolovromann.myshopping.core.ui.model.NavigationItem
import ru.sokolovromann.myshopping.feature.settings.domain.usecase.ExportBackupUseCase
import ru.sokolovromann.myshopping.feature.settings.domain.usecase.ImportBackupUseCase
import ru.sokolovromann.myshopping.feature.settings.domain.usecase.ObserveUserPreferencesUseCase
import ru.sokolovromann.myshopping.feature.settings.domain.usecase.UpdatePreferencesUseCases
import ru.sokolovromann.myshopping.feature.settings.ui.action.AddEditProductSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.action.BackupSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.action.CartsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.action.GeneralSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.action.ProductsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.action.ProductsWidgetSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.action.SuggestionsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.ui.event.SettingsEvent
import ru.sokolovromann.myshopping.feature.settings.ui.mapper.SettingsStateMapper
import ru.sokolovromann.myshopping.feature.settings.ui.model.SettingsState

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    private val updatePreferencesUseCases: UpdatePreferencesUseCases,
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase,
    private val settingsStateMapper: SettingsStateMapper,
    private val navigator: Navigator,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _settingsState: MutableStateFlow<SettingsState> =
        MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState

    private val _settingsEvent = Channel<SettingsEvent>()
    val settingsEvent = _settingsEvent.receiveAsFlow()

    init { observeUserPreferences() }

    fun onNavigationItemSelected(navigationItem: NavigationItem) = when (navigationItem) {
        NavigationItem.Purchases -> navigator.navigateTo(Screen.Purchases)
        NavigationItem.Archive -> navigator.navigateTo(Screen.Archive)
        NavigationItem.Trash -> navigator.navigateTo(Screen.Trash)
        NavigationItem.Suggestions -> navigator.navigateTo(Screen.Suggestions)
        NavigationItem.Settings -> {}
        NavigationItem.About -> navigator.navigateTo(Screen.About)
    }

    fun onBackClick() {
        navigator.navigateBack()
    }

    fun onGeneralSettingsAction(action: GeneralSettingsAction) = viewModelScope.launch {
        when (action) {
            is GeneralSettingsAction.OnThemeSelected ->
                updatePreferencesUseCases.updateGeneral(action.theme)
            is GeneralSettingsAction.OnFontSizeSelected ->
                updatePreferencesUseCases.updateGeneral(action.fontSize)
            is GeneralSettingsAction.OnMoneySelected ->
                updatePreferencesUseCases.updateGeneral(action.formattingMode)
            is GeneralSettingsAction.OnCurrencySaved ->
                updatePreferencesUseCases.updateGeneral(action.currency)
        }
    }

    fun onCartsSettingsAction(action: CartsSettingsAction) = viewModelScope.launch {
        when (action) {
            is CartsSettingsAction.OnAfterAddingSelected ->
                updatePreferencesUseCases.updateCarts(action.afterAdding)
            is CartsSettingsAction.OnAfterCompletingSelected ->
                updatePreferencesUseCases.updateCarts(action.afterCompleting)
            is CartsSettingsAction.OnAfterArchivingSelected ->
                updatePreferencesUseCases.updateCarts(action.afterArchiving)
            is CartsSettingsAction.OnSwipeLeftSelected ->
                updatePreferencesUseCases.updateCarts(action.swipe)
            is CartsSettingsAction.OnSwipeRightSelected ->
                updatePreferencesUseCases.updateCarts(action.swipe)
            is CartsSettingsAction.OnDeletionFromTrashSelected ->
                updatePreferencesUseCases.updateCarts(action.deletionFromTrash)
        }
    }

    fun onProductsSettingsAction(action: ProductsSettingsAction) = viewModelScope.launch {
        when (action) {
            is ProductsSettingsAction.OnAddingModeSelected ->
                updatePreferencesUseCases.updateProducts(action.addingMode)
            is ProductsSettingsAction.OnStrikethroughCompletedSelected ->
                updatePreferencesUseCases.updateProducts(action.strikethroughCompleted)
            is ProductsSettingsAction.OnAfterCompletingSelected ->
                updatePreferencesUseCases.updateProducts(action.afterCompleting)
            is ProductsSettingsAction.OnSwipeLeftSelected ->
                updatePreferencesUseCases.updateProducts(action.swipe)
            is ProductsSettingsAction.OnSwipeRightSelected ->
                updatePreferencesUseCases.updateProducts(action.swipe)
        }
    }

    fun onProductsWidgetSettingsAction(action: ProductsWidgetSettingsAction) = viewModelScope.launch {
        when (action) {
            is ProductsWidgetSettingsAction.OnThemeSelected ->
                updatePreferencesUseCases.updateProductsWidget(action.theme)
            is ProductsWidgetSettingsAction.OnFontSizeSelected ->
                updatePreferencesUseCases.updateProductsWidget(action.fontSize)
            is ProductsWidgetSettingsAction.OnGroupProductsSelected ->
                updatePreferencesUseCases.updateProductsWidget(action.groupProducts)
        }
    }

    fun onAddEditProductSettingsAction(action: AddEditProductSettingsAction) = viewModelScope.launch {
        when (action) {
            is AddEditProductSettingsAction.OnAfterTappingEnterSelected ->
                updatePreferencesUseCases.updateAddEditProduct(action.afterTappingEnter)
            is AddEditProductSettingsAction.OnAfterAddingSelected ->
                updatePreferencesUseCases.updateAddEditProduct(action.afterAdding)
            is AddEditProductSettingsAction.OnAfterEditingSelected ->
                updatePreferencesUseCases.updateAddEditProduct(action.afterEditing)
            is AddEditProductSettingsAction.OnTaxSaved ->
                updatePreferencesUseCases.updateAddEditProduct(action.tax)
        }
    }

    fun onSuggestionsSettingsAction(action: SuggestionsSettingsAction) = viewModelScope.launch {
        when (action) {
            is SuggestionsSettingsAction.OnAddingModeSelected ->
                updatePreferencesUseCases.updateSuggestions(action.addingMode)
        }
    }

    fun onBackupSettingsAction(action: BackupSettingsAction) = viewModelScope.launch {
        when (action) {
            BackupSettingsAction.OnExportBackupClicked -> onExportBackup()
            is BackupSettingsAction.OnImportBackupClicked -> onImportBackup(action.uri)
        }
    }

    private fun observeUserPreferences() = viewModelScope.launch {
        observeUserPreferencesUseCase()
            .onStart {
                withContext(mainDispatcher) {
                    val state = SettingsState(isProgress = true)
                    _settingsState.tryEmit(state)
                }
            }
            .collect { userPreferences ->
                withContext(mainDispatcher) {
                    val state = settingsStateMapper.createState(userPreferences)
                    _settingsState.tryEmit(state)
                }
            }
    }

    private suspend fun onExportBackup() {
        val progressState = _settingsState.value.copy(isProgress = true)
        _settingsState.tryEmit(progressState)

        val result = exportBackupUseCase()
        val resultState = _settingsState.value.copy(isProgress = false)
        _settingsState.tryEmit(resultState)

        if (result) {
            _settingsEvent.send(SettingsEvent.ShowExportBackupSuccessMessage)
        } else {
            _settingsEvent.send(SettingsEvent.ShowBackupFailureMessage)
        }
    }

    private suspend fun onImportBackup(uri: Uri) {
        val progressState = _settingsState.value.copy(isProgress = true)
        _settingsState.tryEmit(progressState)

        val result = importBackupUseCase(uri.toString())
        val resultState = _settingsState.value.copy(isProgress = false)
        _settingsState.tryEmit(resultState)

        if (result) {
            _settingsEvent.send(SettingsEvent.ShowImportBackupSuccessMessage)
        } else {
            _settingsEvent.send(SettingsEvent.ShowBackupFailureMessage)
        }
    }
}