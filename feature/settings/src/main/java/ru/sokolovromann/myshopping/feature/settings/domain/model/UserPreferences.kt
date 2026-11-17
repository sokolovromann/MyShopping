package ru.sokolovromann.myshopping.feature.settings.domain.model

import ru.sokolovromann.myshopping.core.domain.model.AddEditProductPreferences
import ru.sokolovromann.myshopping.core.domain.model.BackupPreferences
import ru.sokolovromann.myshopping.core.domain.model.CartsPreferences
import ru.sokolovromann.myshopping.core.domain.model.GeneralPreferences
import ru.sokolovromann.myshopping.core.domain.model.ProductsPreferences
import ru.sokolovromann.myshopping.core.domain.model.ProductsWidgetPreferences
import ru.sokolovromann.myshopping.core.domain.model.SuggestionsPreferences

data class UserPreferences(
    val general: GeneralPreferences,
    val carts: CartsPreferences,
    val products: ProductsPreferences,
    val productsWidget: ProductsWidgetPreferences,
    val addEditProduct: AddEditProductPreferences,
    val suggestions: SuggestionsPreferences,
    val backup: BackupPreferences
)