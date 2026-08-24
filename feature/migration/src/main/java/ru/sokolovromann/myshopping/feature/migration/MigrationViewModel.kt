package ru.sokolovromann.myshopping.feature.migration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.sokolovromann.myshopping.core.domain.di.MainDispatcher
import ru.sokolovromann.myshopping.core.domain.usecase.InsertCartsUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.InsertFabricsUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.InsertProductsUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.InsertSuggestionsUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.UpdateAddEditProductPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.UpdateBackupPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.UpdateCartsPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.UpdateGeneralPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.UpdateProductsPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.UpdateProductsWidgetPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.UpdateSuggestionsPreferencesUseCase
import ru.sokolovromann.myshopping.core.domain.usecase.UpdateUserConfigUseCase
import ru.sokolovromann.myshopping.core.navigation.Navigator
import ru.sokolovromann.myshopping.core.navigation.Screen
import ru.sokolovromann.myshopping.old.api15.usecase.GetApi15DataUseCase

@HiltViewModel
class MigrationViewModel @Inject constructor(
    private val getApi15DataUseCase: GetApi15DataUseCase,
    private val insertCartsUseCase: InsertCartsUseCase,
    private val insertProductsUseCase: InsertProductsUseCase,
    private val insertSuggestionsUseCase: InsertSuggestionsUseCase,
    private val insertFabricsUseCase: InsertFabricsUseCase,
    private val updateUserConfigUseCase: UpdateUserConfigUseCase,
    private val migrationMapper: MigrationMapper,
    private val updateAddEditProductPreferencesUseCase: UpdateAddEditProductPreferencesUseCase,
    private val updateBackupPreferencesUseCase: UpdateBackupPreferencesUseCase,
    private val updateCartsPreferencesUseCase: UpdateCartsPreferencesUseCase,
    private val updateGeneralPreferencesUseCase: UpdateGeneralPreferencesUseCase,
    private val updateProductsPreferencesUseCase: UpdateProductsPreferencesUseCase,
    private val updateProductsWidgetPreferencesUseCase: UpdateProductsWidgetPreferencesUseCase,
    private val updateSuggestionsPreferencesUseCase: UpdateSuggestionsPreferencesUseCase,
    private val navigator: Navigator,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<MigrationUiState>(MigrationUiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun onMigrate() = viewModelScope.launch(mainDispatcher) {
        _uiState.emit(MigrationUiState.Migrating)

        val api15Data = getApi15DataUseCase()

        val carts = migrationMapper.toCarts(api15Data.shoppings)
        insertCartsUseCase(carts)

        val products = migrationMapper.toProducts(api15Data.products)
        insertProductsUseCase(products)

        when (api15Data.appBuildConfig.userCodeVersion) {
            in 15..38 -> {
                migrationMapper.toSuggestionsWithFabricsFromAutocompletes(api15Data.autocompletes).forEach {
                    insertSuggestionsUseCase(it.key)
                    insertFabricsUseCase(it.value)
                }
            }
            in 39..41 -> {
                val suggestions = migrationMapper.toSuggestionsFromOld(api15Data.suggestions)
                insertSuggestionsUseCase(suggestions)

                val fabrics = migrationMapper.toFabricsFromDetails(api15Data.suggestionDetails)
                insertFabricsUseCase(fabrics)
            }
            else -> {}
        }

        val userConfig = migrationMapper.toUserConfig(api15Data.appBuildConfig)
        updateUserConfigUseCase(userConfig)

        val userPreferences = migrationMapper.toUserPreferences(api15Data.userPreferences)
        updateAddEditProductPreferencesUseCase(userPreferences.addEditProduct)
        updateBackupPreferencesUseCase(userPreferences.backupPreferences)
        updateCartsPreferencesUseCase(userPreferences.carts)
        updateGeneralPreferencesUseCase(userPreferences.general)
        updateProductsPreferencesUseCase(userPreferences.products)
        updateProductsWidgetPreferencesUseCase(userPreferences.productsWidget)
        updateSuggestionsPreferencesUseCase(userPreferences.suggestions)

        _uiState.emit(MigrationUiState.Finish)
    }

    fun onOpenPurchases() {
        navigator.navigateTo(Screen.Purchases)
    }

    fun onCancel() {
        if (_uiState.value == MigrationUiState.Initial) {
            navigator.navigateBack()
        }
    }
}