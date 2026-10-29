package ru.sokolovromann.myshopping.feature.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.sokolovromann.myshopping.core.domain.di.MainDispatcher
import ru.sokolovromann.myshopping.core.domain.usecase.ExportBackupUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.ImportBackupUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.ObserveUserPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.UpdateAddEditProductPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.UpdateCartsPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.UpdateGeneralPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.UpdateProductsPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.UpdateProductsWidgetPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.UpdateSuggestionsPreferencesUseCase
import ru.sokolovromann.myshopping.core.navigation.Navigator
import ru.sokolovromann.myshopping.core.navigation.Screen
import ru.sokolovromann.myshopping.core.ui.model.NavigationItem
import ru.sokolovromann.myshopping.feature.settings.action.AddEditProductSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.BackupSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.CartsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.GeneralSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.ProductsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.ProductsWidgetSettingsAction
import ru.sokolovromann.myshopping.feature.settings.action.SuggestionsSettingsAction
import ru.sokolovromann.myshopping.feature.settings.event.SettingsEvent
import ru.sokolovromann.myshopping.feature.settings.mapper.SettingsMapper
import ru.sokolovromann.myshopping.feature.settings.model.SettingsState

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    private val updateGeneralPreferencesUseCase: UpdateGeneralPreferencesUseCase,
    private val updateCartsPreferencesUseCase: UpdateCartsPreferencesUseCase,
    private val updateProductsPreferencesUseCase: UpdateProductsPreferencesUseCase,
    private val updateProductsWidgetPreferencesUseCase: UpdateProductsWidgetPreferencesUseCase,
    private val updateAddEditProductPreferencesUseCase: UpdateAddEditProductPreferencesUseCase,
    private val updateSuggestionsPreferencesUseCase: UpdateSuggestionsPreferencesUseCase,
    private val exportBackupUseCase: ExportBackupUseCase,
    private val importBackupUseCase: ImportBackupUseCase,
    private val settingsMapper: SettingsMapper,
    private val navigator: Navigator,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _settingsState: MutableStateFlow<SettingsState> = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState

    private val _settingsEvent = Channel<SettingsEvent>()
    val settingsEvent = _settingsEvent.receiveAsFlow()

    init { onInit() }

    fun onNavigationItemSelected(navigationItem: NavigationItem) = when (navigationItem) {
        NavigationItem.Purchases -> navigator.navigateTo(Screen.Purchases)
        NavigationItem.Archive -> navigator.navigateTo(Screen.Archive)
        NavigationItem.Trash -> navigator.navigateTo(Screen.Trash)
        NavigationItem.Dictionary -> navigator.navigateTo(Screen.Dictionary)
        NavigationItem.Settings -> {}
        NavigationItem.About -> navigator.navigateTo(Screen.About)
    }

    fun onBackClick() {
        navigator.navigateBack()
    }

    fun onGeneralSettingsActions(action: GeneralSettingsAction) = viewModelScope.launch {
        when (action) {
            is GeneralSettingsAction.OnThemeSelected ->
                updateGeneralPreferencesUseCase(action.theme)
            is GeneralSettingsAction.OnFontSizeSelected ->
                updateGeneralPreferencesUseCase(action.fontSize)
            is GeneralSettingsAction.OnDateTimeSelected ->
                updateGeneralPreferencesUseCase(action.formattingMode)
            is GeneralSettingsAction.OnMoneySelected ->
                updateGeneralPreferencesUseCase(action.formattingMode)
            is GeneralSettingsAction.OnCurrencySaved ->
                updateGeneralPreferencesUseCase(action.currency)
            is GeneralSettingsAction.OnKeyboardDisplayDelaySelected ->
                updateGeneralPreferencesUseCase(action.displayDelay)
        }
    }

    fun onCartsSettingsAction(action: CartsSettingsAction) = viewModelScope.launch {
        when (action) {
            is CartsSettingsAction.OnAfterAddingSelected ->
                updateCartsPreferencesUseCase(action.afterAdding)
            is CartsSettingsAction.OnAfterCompletingSelected ->
                updateCartsPreferencesUseCase(action.afterCompleting)
            is CartsSettingsAction.OnAfterArchivingSelected ->
                updateCartsPreferencesUseCase(action.afterArchiving)
            is CartsSettingsAction.OnTappingCheckboxSelected ->
                updateCartsPreferencesUseCase(action.afterTappingCheckbox)
            is CartsSettingsAction.OnCheckboxColorSelected ->
                updateCartsPreferencesUseCase(action.checkboxColor)
            is CartsSettingsAction.OnSwipeLeftSelected ->
                updateCartsPreferencesUseCase(action.swipe)
            is CartsSettingsAction.OnSwipeRightSelected ->
                updateCartsPreferencesUseCase(action.swipe)
            is CartsSettingsAction.OnDeletionFromTrashSelected ->
                updateCartsPreferencesUseCase(action.deletionFromTrash)
        }
    }

    fun onProductsSettingsAction(action: ProductsSettingsAction) = viewModelScope.launch {
        when (action) {
            is ProductsSettingsAction.OnAddingModeSelected ->
                updateProductsPreferencesUseCase(action.addingMode)
            is ProductsSettingsAction.OnStrikethroughCompletedSelected ->
                updateProductsPreferencesUseCase(action.strikethroughCompleted)
            is ProductsSettingsAction.OnAfterCompletingSelected ->
                updateProductsPreferencesUseCase(action.afterCompleting)
            is ProductsSettingsAction.OnAfterTappingCheckboxSelected ->
                updateProductsPreferencesUseCase(action.afterTappingCheckbox)
            is ProductsSettingsAction.OnCheckboxColorSelected ->
                updateProductsPreferencesUseCase(action.checkboxColor)
            is ProductsSettingsAction.OnAfterTappingItemSelected ->
                updateProductsPreferencesUseCase(action.afterTappingItem)
            is ProductsSettingsAction.OnSwipeLeftSelected ->
                updateProductsPreferencesUseCase(action.swipe)
            is ProductsSettingsAction.OnSwipeRightSelected ->
                updateProductsPreferencesUseCase(action.swipe)
        }
    }

    fun onProductsWidgetSettingsAction(action: ProductsWidgetSettingsAction) = viewModelScope.launch {
        when (action) {
            is ProductsWidgetSettingsAction.OnThemeSelected ->
                updateProductsWidgetPreferencesUseCase(action.theme)
            is ProductsWidgetSettingsAction.OnFontSizeSelected ->
                updateProductsWidgetPreferencesUseCase(action.fontSize)
            is ProductsWidgetSettingsAction.OnGroupProductsSelected ->
                updateProductsWidgetPreferencesUseCase(action.groupProducts)
        }
    }

    fun onAddEditProductSettingsAction(action: AddEditProductSettingsAction) = viewModelScope.launch {
        when (action) {
            is AddEditProductSettingsAction.OnAfterTappingEnterSelected ->
                updateAddEditProductPreferencesUseCase(action.afterTappingEnter)
            is AddEditProductSettingsAction.OnAfterAddingSelected ->
                updateAddEditProductPreferencesUseCase(action.afterAdding)
            is AddEditProductSettingsAction.OnAfterEditingSelected ->
                updateAddEditProductPreferencesUseCase(action.afterEditing)
            is AddEditProductSettingsAction.OnTaxSaved ->
                updateAddEditProductPreferencesUseCase(action.tax)
        }
    }

    fun onSuggestionsSettingsAction(action: SuggestionsSettingsAction) = viewModelScope.launch {
        when (action) {
            is SuggestionsSettingsAction.OnAddingModeSelected ->
                updateSuggestionsPreferencesUseCase(action.addingMode)
            is SuggestionsSettingsAction.OnDisplayNamesSelected ->
                updateSuggestionsPreferencesUseCase(action.displayNames)
            is SuggestionsSettingsAction.OnDisplayDetailsSelected ->
                updateSuggestionsPreferencesUseCase(action.displayDetails)
        }
    }

    fun onBackupSettingsAction(action: BackupSettingsAction) = viewModelScope.launch {
        when (action) {
            BackupSettingsAction.OnExportBackupClicked -> onExportBackup()
            is BackupSettingsAction.OnImportBackupClicked -> onImportBackup(action.uri)
        }
    }

    private fun onInit() = viewModelScope.launch {
        observeUserPreferencesUseCase().collect { userPreferences ->
            withContext(mainDispatcher) {
                val state = settingsMapper.createSettingsState(userPreferences)
                _settingsState.tryEmit(state)
            }
        }
    }

    private suspend fun onExportBackup() {
        val oldState = _settingsState.value
        _settingsState.tryEmit(oldState.copy(isLoading = true))

        val result = exportBackupUseCase()
        _settingsState.tryEmit(oldState.copy(isLoading = false))

        if (result) {
            _settingsEvent.send(SettingsEvent.ShowExportBackupSuccessMessage)
        } else {
            _settingsEvent.send(SettingsEvent.ShowBackupFailureMessage)
        }
    }

    private suspend fun onImportBackup(uri: Uri) {
        val oldState = _settingsState.value
        _settingsState.tryEmit(oldState.copy(isLoading = true))

        val result = importBackupUseCase(uri.toString())
        _settingsState.tryEmit(oldState.copy(isLoading = false))

        if (result) {
            _settingsEvent.send(SettingsEvent.ShowImportBackupSuccessMessage)
        } else {
            _settingsEvent.send(SettingsEvent.ShowBackupFailureMessage)
        }
    }
}