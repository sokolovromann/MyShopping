package ru.sokolovromann.myshopping.core.domain.model

data class UserPreferences(
    val general: GeneralPreferences,
    val carts: CartsPreferences,
    val products: ProductsPreferences,
    val productsWidget: ProductsWidgetPreferences,
    val addEditProduct: AddEditProductPreferences,
    val suggestions: SuggestionsPreferences,
    val backupPreferences: BackupPreferences
)