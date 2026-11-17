package ru.sokolovromann.myshopping.feature.settings.ui.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.feature.settings.domain.model.UserPreferences
import ru.sokolovromann.myshopping.feature.settings.ui.model.SettingsState

class SettingsStateMapper @Inject constructor(
    private val general: UiGeneralPreferencesMapper,
    private val carts: UiCartsPreferencesMapper,
    private val products: UiProductsPreferencesMapper,
    private val productsWidget: UiProductsWidgetPreferencesMapper,
    private val addEditProduct: UiAddEditProductPreferencesMapper,
    private val suggestions: UiSuggestionsPreferencesMapper,
    private val backup: UiBackupPreferencesMapper
) {

    fun createState(preferences: UserPreferences) = SettingsState(
        isProgress = false,
        generalPreferences = general.toPreferences(preferences.general),
        cartsPreferences = carts.toPreferences(preferences.carts),
        productPreferences = products.toPreferences(preferences.products),
        productsWidgetPreferences = productsWidget.toPreferences(preferences.productsWidget),
        addEditProductPreferences = addEditProduct.toPreferences(
            preferences.addEditProduct,
            preferences.general.moneyFormattingMode
        ),
        suggestionsPreferences = suggestions.toPreferences(preferences.suggestions),
        backupPreferences = backup.toPreferences(preferences.backup)
    )
}