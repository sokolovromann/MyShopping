package ru.sokolovromann.myshopping.feature.settings.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.UserPreferences
import ru.sokolovromann.myshopping.feature.settings.model.SettingsState

class SettingsMapper @Inject constructor(
    private val general: GeneralSettingsMapper,
    private val carts: CartsSettingsMapper,
    private val products: ProductsSettingsMapper,
    private val productsWidget: ProductsWidgetSettingsMapper,
    private val addEditProduct: AddEditProductSettingsMapper,
    private val suggestions: SuggestionsSettingsMapper,
    private val backup: BackupSettingsMapper
) {

    fun createSettingsState(userPreferences: UserPreferences) = SettingsState(
        isLoading = false,
        generalItems = general.toItems(userPreferences.general),
        cartsItems = carts.toItems(userPreferences.carts),
        productsItems = products.toItems(userPreferences.products),
        productsWidgetItems = productsWidget.toItems(userPreferences.productsWidget),
        addEditProductItems = addEditProduct.toItems(
            userPreferences.addEditProduct,
            userPreferences.general.moneyFormattingMode
        ),
        suggestionsItems = suggestions.toItems(userPreferences.suggestions),
        backupItems = backup.toItems(userPreferences.backupPreferences)
    )
}