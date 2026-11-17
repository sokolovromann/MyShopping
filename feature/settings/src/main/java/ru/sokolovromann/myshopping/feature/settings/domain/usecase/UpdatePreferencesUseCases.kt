package ru.sokolovromann.myshopping.feature.settings.domain.usecase

import jakarta.inject.Inject

data class UpdatePreferencesUseCases @Inject constructor(
    val updateGeneral: UpdateGeneralPreferencesUseCase,
    val updateCarts: UpdateCartsPreferencesUseCase,
    val updateProducts: UpdateProductsPreferencesUseCase,
    val updateProductsWidget: UpdateProductsWidgetPreferencesUseCase,
    val updateAddEditProduct: UpdateAddEditProductPreferencesUseCase,
    val updateSuggestions: UpdateSuggestionsPreferencesUseCase
)