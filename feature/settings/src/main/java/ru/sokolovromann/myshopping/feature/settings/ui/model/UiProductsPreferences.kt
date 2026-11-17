package ru.sokolovromann.myshopping.feature.settings.ui.model

import androidx.compose.runtime.Immutable
import ru.sokolovromann.myshopping.core.domain.model.AfterCompletingProduct
import ru.sokolovromann.myshopping.core.domain.model.ProductsAddingMode
import ru.sokolovromann.myshopping.core.domain.model.StrikethroughCompletedProducts
import ru.sokolovromann.myshopping.core.domain.model.SwipeProduct
import ru.sokolovromann.myshopping.core.domain.model.UserPreferencesDefaults

@Immutable
data class UiProductsPreferences(
    val addingMode: UiPreference<ProductsAddingMode> = UiPreference(UserPreferencesDefaults.Products.addingMode),
    val strikethroughCompleted: UiPreference<StrikethroughCompletedProducts> = UiPreference(UserPreferencesDefaults.Products.strikethroughCompleted),
    val afterCompleting: UiPreference<AfterCompletingProduct> = UiPreference(UserPreferencesDefaults.Products.afterCompleting),
    val swipeLeft: UiPreference<SwipeProduct.Left> = UiPreference(UserPreferencesDefaults.Products.swipeLeft),
    val swipeRight: UiPreference<SwipeProduct.Right> = UiPreference(UserPreferencesDefaults.Products.swipeRight)
)