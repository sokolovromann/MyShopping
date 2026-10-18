package ru.sokolovromann.myshopping.feature.settings.mapper

import jakarta.inject.Inject
import ru.sokolovromann.myshopping.core.domain.model.UserPreferences
import ru.sokolovromann.myshopping.core.ui.model.UiText
import ru.sokolovromann.myshopping.feature.settings.R
import ru.sokolovromann.myshopping.feature.settings.model.SettingsState

class SettingsMapper @Inject constructor(
    private val general: GeneralSettingsMapper,
    private val carts: CartsSettingsMapper,
    private val products: ProductsSettingsMapper
) {

    fun createSettingsState(userPreferences: UserPreferences) = SettingsState(
        isLoading = false,
        generalItems = general.toItems(userPreferences.general),
        cartsItems = carts.toItems(userPreferences.carts),
        productsItems = products.toItems(userPreferences.products)
    )
}